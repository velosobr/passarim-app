// O ViewModel do Explorar.
//
// Um ViewModel guarda o estado da tela e sobrevive à rotação. viewModelScope
// é o escopo das coroutines dele: tudo o que roda ali é cancelado quando a
// tela sai de vez. MutableStateFlow guarda o estado atual (a tela lê pelo
// StateFlow, só leitura); Channel entrega cada evento uma vez só.

package com.velosobr.passarim.feature.explore.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.onFailure
import com.velosobr.passarim.core.domain.onSuccess
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.core.presentation.toScreenError
import com.velosobr.passarim.feature.explore.domain.ExploreRepository
import com.velosobr.passarim.feature.explore.domain.MAX_QUERY_LENGTH
import com.velosobr.passarim.feature.explore.domain.sanitizeQuery
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Espera sem digitar antes de buscar. */
const val SEARCH_DEBOUNCE_MILLIS = 400L

/** A próxima página é pedida quando o último item visível está a esta distância do fim. */
const val PREFETCH_DISTANCE = 6

// Chaves do SavedStateHandle: busca e filtros sobrevivem à morte do processo.
const val KEY_QUERY = "explore.query"
const val KEY_BIOME = "explore.biome"
const val KEY_STATE = "explore.state"

/** Regra da paginação, separada da UI para ser testada. */
fun shouldLoadMore(
    lastVisibleIndex: Int,
    itemCount: Int,
): Boolean = itemCount > 0 && lastVisibleIndex >= itemCount - PREFETCH_DISTANCE

/** O que define a lista: mudou, a lista recomeça do zero. */
private data class Criteria(
    val query: String,
    val biome: String?,
    val state: String?,
)

/**
 * Explorar: busca com debounce, filtros, paginação por cursor e favoritos.
 *
 * Há **um só** [loadJob] por vez (primeira página ou página seguinte): uma busca nova o cancela, então a
 * resposta de uma busca antiga nunca sobrescreve a atual, e a paginação não corre em paralelo com ela.
 */
@OptIn(FlowPreview::class)
class ExploreViewModel(
    private val explore: ExploreRepository,
    private val filtersRepository: FiltersRepository,
    private val favorites: FavoritesRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            ExploreState(query = savedState[KEY_QUERY] ?: "", biome = savedState[KEY_BIOME], state = savedState[KEY_STATE]),
        )
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private val eventChannel = Channel<ExploreEvent>(Channel.BUFFERED)
    val events: Flow<ExploreEvent> = eventChannel.receiveAsFlow()

    /** O texto cru do campo; o debounce é aplicado sobre ele (a primeira emissão não espera). */
    private val queryText = MutableStateFlow(_state.value.query)
    private var criteria = Criteria("", null, null)
    private var loadJob: Job? = null
    private var retryJob: Job? = null

    // Monta a busca: a primeira emissão sai na hora; as seguintes esperam
    // SEARCH_DEBOUNCE_MILLIS sem digitação (debounce), para não fazer um pedido
    // por tecla. combine junta busca, bioma e UF, e distinctUntilChanged evita
    // recarregar quando nada mudou de fato.
    init {
        val query =
            queryText
                .drop(1)
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .onStart { emit(queryText.value) }
                .map(::sanitizeQuery)
        val biome = _state.map { it.biome }.distinctUntilChanged()
        val uf = _state.map { it.state }.distinctUntilChanged()
        combine(query, biome, uf, ::Criteria)
            .distinctUntilChanged()
            .onEach(::loadFirstPage)
            .launchIn(viewModelScope)
        favorites
            .observeIds()
            .onEach { ids -> _state.update { it.copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)
        loadFilters()
    }

    /** Trata cada ação da tela. */
    @Suppress("CyclomaticComplexMethod")
    fun onAction(action: ExploreAction) {
        when (action) {
            is ExploreAction.QueryChange -> setQuery(action.text)
            ExploreAction.LoadMore -> loadMore()
            ExploreAction.RetryLoadMore -> {
                _state.update { it.copy(loadMoreError = null) }
                loadMore()
            }
            ExploreAction.Retry -> if (_state.value.retryEnabled) loadFirstPage(criteria)
            ExploreAction.ClearFilters -> setFilters(biome = null, state = null)
            ExploreAction.ClearBiome -> setFilters(biome = null, state = _state.value.state)
            ExploreAction.ClearState -> setFilters(biome = _state.value.biome, state = null)
            ExploreAction.OpenFilters -> openSheet()
            ExploreAction.DismissFilters -> _state.update { it.copy(sheet = null) }
            is ExploreAction.SheetBiome -> updateSheet { it.copy(biome = action.code.takeIf { code -> code != it.biome }) }
            is ExploreAction.SheetState -> updateSheet { it.copy(state = action.code.takeIf { code -> code != it.state }) }
            ExploreAction.ShowAllStates -> updateSheet { it.copy(showAllStates = true) }
            ExploreAction.ClearSheet -> updateSheet { it.copy(biome = null, state = null) }
            ExploreAction.ApplyFilters -> applySheet()
            ExploreAction.RetryFilters -> loadFilters()
            is ExploreAction.ToggleFavorite -> viewModelScope.launch { favorites.toggle(action.summary) }
            is ExploreAction.OpenDetail -> eventChannel.trySend(ExploreEvent.NavigateToDetail(action.id))
            ExploreAction.OpenFavorites -> eventChannel.trySend(ExploreEvent.NavigateToFavorites)
        }
    }

    /** Guarda o texto (também no SavedStateHandle) e o entrega ao debounce. */
    private fun setQuery(text: String) {
        val limited = text.take(MAX_QUERY_LENGTH)
        savedState[KEY_QUERY] = limited
        _state.update { it.copy(query = limited) }
        queryText.value = limited
    }

    /** Mudar bioma ou estado dispara a recarga pelo `combine` do `init`. */
    private fun setFilters(
        biome: String?,
        state: String?,
    ) {
        savedState[KEY_BIOME] = biome
        savedState[KEY_STATE] = state
        _state.update { it.copy(biome = biome, state = state, sheet = null) }
    }

    /** Abre o sheet com as escolhas atuais; se os filtros falharam antes, tenta de novo. */
    private fun openSheet() {
        _state.update { it.copy(sheet = FiltersSheetState(biome = it.biome, state = it.state)) }
        if (_state.value.filters == FiltersPhase.Error) loadFilters()
    }

    /** Muda as escolhas do sheet aberto (nada acontece se ele estiver fechado). */
    private fun updateSheet(change: (FiltersSheetState) -> FiltersSheetState) {
        _state.update { s -> s.copy(sheet = s.sheet?.let(change)) }
    }

    /** "Mostrar aves": as escolhas do sheet viram os filtros da lista. */
    private fun applySheet() {
        val sheet = _state.value.sheet ?: return
        setFilters(sheet.biome, sheet.state)
    }

    /** Recomeça a lista do zero para os critérios novos, cancelando o pedido anterior. */
    private fun loadFirstPage(next: Criteria) {
        criteria = next
        loadJob?.cancel()
        retryJob?.cancel()
        _state.update {
            it.copy(
                items = emptyList(),
                nextCursor = null,
                phase = ExplorePhase.Loading,
                isLoadingMore = false,
                loadMoreError = null,
                retryEnabled = true,
            )
        }
        loadJob =
            viewModelScope.launch {
                explore
                    .list(next.query, next.biome, next.state, cursor = null)
                    .onSuccess { page ->
                        _state.update {
                            it.copy(
                                items = page.items.distinctBy { item -> item.id },
                                nextCursor = page.nextCursor,
                                phase = if (page.items.isEmpty()) ExplorePhase.Empty else ExplorePhase.Content,
                            )
                        }
                    }.onFailure { showError(it.toScreenError()) }
            }
    }

    /** Pede a próxima página, se houver cursor e nada estiver em andamento. */
    private fun loadMore() {
        val s = _state.value
        val cursor = s.nextCursor
        // Só pede com a grade na tela, sem outra requisição em curso e sem erro pendente (esse espera o botão).
        val idle = loadJob?.isActive != true && s.loadMoreError == null
        if (!idle || cursor == null || s.phase != ExplorePhase.Content) return
        _state.update { it.copy(isLoadingMore = true) }
        loadJob =
            viewModelScope.launch {
                explore
                    .list(criteria.query, criteria.biome, criteria.state, cursor = cursor)
                    .onSuccess { page ->
                        _state.update {
                            // Um `key = id` repetido derruba a LazyVerticalGrid: ids já vistos são descartados.
                            val seen = it.items.mapTo(HashSet()) { item -> item.id }
                            it.copy(
                                items = it.items + page.items.filter { item -> seen.add(item.id) },
                                nextCursor = page.nextCursor,
                                isLoadingMore = false,
                            )
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(isLoadingMore = false, loadMoreError = error.toScreenError()) }
                    }
            }
    }

    /**
     * Mostra a tela de erro. Com RateLimited, o botão de tentar fica desligado
     * pelos segundos do Retry-After e religa sozinho depois.
     */
    private fun showError(error: ScreenError) {
        val wait = (error as? ScreenError.RateLimited)?.retryAfterSeconds
        _state.update { it.copy(phase = ExplorePhase.Error(error), retryEnabled = wait == null) }
        if (wait != null) {
            retryJob =
                viewModelScope.launch {
                    delay(wait * 1_000L)
                    _state.update { it.copy(retryEnabled = true) }
                }
        }
    }

    /** Carrega as opções de filtro, independente da lista. */
    private fun loadFilters() {
        _state.update { it.copy(filters = FiltersPhase.Loading) }
        viewModelScope.launch {
            filtersRepository
                .get()
                .onSuccess { f -> _state.update { it.copy(filters = FiltersPhase.Loaded(f)) } }
                .onFailure { _state.update { it.copy(filters = FiltersPhase.Error) } }
        }
    }
}
