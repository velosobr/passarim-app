package com.velosobr.passarim.feature.explore.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasLength
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.StateFilter
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.explore.domain.SpeciesPage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * O `Main` de teste é um `UnconfinedTestDispatcher`: o `viewModelScope` roda na hora, e o `runTest`
 * usa o mesmo relógio virtual, então `advanceTimeBy` controla o debounce e o `Retry-After`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {
    private val repo = FakeExploreRepository()
    private val filters = FakeFiltersRepository()
    private val favorites = FakeFavoritesRepository()

    private fun viewModel(saved: SavedStateHandle = SavedStateHandle()) = ExploreViewModel(repo, filters, favorites, saved)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Passa o debounce da busca. */
    private fun TestScope.typed() = advanceTimeBy(SEARCH_DEBOUNCE_MILLIS + 1)

    // --- Primeira página e busca ---

    @Test
    fun `ao abrir carrega a primeira pagina sem busca nem filtros`() =
        runTest {
            val vm = viewModel()
            assertThat(repo.calls).containsExactly(ListCall("", null, null, null))
            val s = vm.state.value
            assertThat(s.phase).isEqualTo(ExplorePhase.Content)
            assertThat(s.items).hasSize(20)
            assertThat(s.nextCursor).isEqualTo("c1")
        }

    @Test
    fun `a busca espera 400 ms sem digitar antes de pedir`() =
        runTest {
            val vm = viewModel()
            vm.onAction(ExploreAction.QueryChange("a"))
            advanceTimeBy(200)
            vm.onAction(ExploreAction.QueryChange("az"))
            advanceTimeBy(SEARCH_DEBOUNCE_MILLIS - 1)
            assertThat(repo.calls).hasSize(1)

            advanceTimeBy(2)
            assertThat(repo.calls).containsExactly(ListCall("", null, null, null), ListCall("az", null, null, null))
            assertThat(vm.state.value.query).isEqualTo("az")
        }

    @Test
    fun `uma busca nova cancela a anterior que ainda nao respondeu`() =
        runTest {
            repo.gates[1] = CompletableDeferred()
            val vm = viewModel()
            vm.onAction(ExploreAction.QueryChange("arara"))
            typed()
            repo.pages[null] = Result.Success(SpeciesPage(listOf(bird("gralha-azul")), null))
            vm.onAction(ExploreAction.QueryChange("azul"))
            typed()

            repo.gates.getValue(1).complete(Unit)
            advanceUntilIdle()

            assertThat(repo.calls.map { it.query }).containsExactly("", "arara", "azul")
            assertThat(
                vm.state.value.items
                    .map { it.id },
            ).containsExactly("gralha-azul")
        }

    @Test
    fun `caracteres de controle e espacos das pontas nao vao para o BFF`() =
        runTest {
            val vm = viewModel()
            vm.onAction(ExploreAction.QueryChange("  arara\nazul "))
            typed()
            assertThat(repo.calls.last().query).isEqualTo("arara azul")
        }

    @Test
    fun `o campo guarda no maximo 100 caracteres`() =
        runTest {
            val vm = viewModel()
            vm.onAction(ExploreAction.QueryChange("a".repeat(150)))
            assertThat(vm.state.value.query).hasLength(100)
        }

    @Test
    fun `enquanto a primeira pagina nao chega a fase e Loading`() =
        runTest {
            repo.gates[0] = CompletableDeferred()
            val vm = viewModel()
            vm.state.test {
                assertThat(awaitItem().phase).isEqualTo(ExplorePhase.Loading)
                repo.gates.getValue(0).complete(Unit)
                assertThat(awaitItem().phase).isEqualTo(ExplorePhase.Content)
            }
        }

    @Test
    fun `pagina vazia vira Empty`() =
        runTest {
            repo.pages[null] = Result.Success(SpeciesPage(emptyList(), null))
            val vm = viewModel()
            assertThat(vm.state.value.phase).isEqualTo(ExplorePhase.Empty)
        }

    // --- Paginação ---

    @Test
    fun `pagina ate o fim e para quando nextCursor e nulo`() =
        runTest {
            repo.pages["c1"] = Result.Success(page(20, 40, "c2"))
            repo.pages["c2"] = Result.Success(page(40, 45, null))
            val vm = viewModel()

            vm.onAction(ExploreAction.LoadMore)
            vm.onAction(ExploreAction.LoadMore)
            vm.onAction(ExploreAction.LoadMore)

            assertThat(repo.calls.map { it.cursor }).containsExactly(null, "c1", "c2")
            assertThat(vm.state.value.items).hasSize(45)
            assertThat(vm.state.value.nextCursor).isNull()
        }

    @Test
    fun `ids repetidos entre paginas sao descartados`() =
        runTest {
            // A página 2 repete "ave-19" (da página 1) e traz "ave-20" duas vezes.
            repo.pages["c1"] = Result.Success(SpeciesPage(listOf(bird("ave-19"), bird("ave-20"), bird("ave-20")), null))
            val vm = viewModel()
            vm.onAction(ExploreAction.LoadMore)

            val ids =
                vm.state.value.items
                    .map { it.id }
            assertThat(ids).hasSize(21)
            assertThat(ids.toSet()).hasSize(21)
        }

    @Test
    fun `pedir a proxima pagina com uma requisicao em curso e ignorado`() =
        runTest {
            repo.pages["c1"] = Result.Success(page(20, 40, null))
            repo.gates[1] = CompletableDeferred()
            val vm = viewModel()

            vm.onAction(ExploreAction.LoadMore)
            assertThat(vm.state.value.isLoadingMore).isTrue()
            vm.onAction(ExploreAction.LoadMore)
            repo.gates.getValue(1).complete(Unit)

            assertThat(repo.calls).hasSize(2)
            assertThat(vm.state.value.isLoadingMore).isFalse()
        }

    @Test
    fun `erro na pagina seguinte preserva os itens e tentar de novo continua`() =
        runTest {
            repo.pages["c1"] = Result.Failure(DataError.Network.Timeout)
            val vm = viewModel()

            vm.onAction(ExploreAction.LoadMore)
            assertThat(vm.state.value.items).hasSize(20)
            assertThat(vm.state.value.phase).isEqualTo(ExplorePhase.Content)
            assertThat(vm.state.value.loadMoreError).isEqualTo(ScreenError.NoConnection)

            // Rolar de novo não repete o pedido sozinho: só o botão da linha de erro.
            vm.onAction(ExploreAction.LoadMore)
            assertThat(repo.calls).hasSize(2)

            repo.pages["c1"] = Result.Success(page(20, 30, null))
            vm.onAction(ExploreAction.RetryLoadMore)
            assertThat(vm.state.value.items).hasSize(30)
            assertThat(vm.state.value.loadMoreError).isNull()
        }

    @Test
    fun `mudar o filtro zera a lista e o cursor e pede a primeira pagina`() =
        runTest {
            repo.pages["c1"] = Result.Success(page(20, 40, "c2"))
            val vm = viewModel()
            vm.onAction(ExploreAction.LoadMore)
            repo.gates[2] = CompletableDeferred()

            vm.onAction(ExploreAction.OpenFilters)
            vm.onAction(ExploreAction.SheetBiome("cerrado"))
            vm.onAction(ExploreAction.ApplyFilters)

            val s = vm.state.value
            assertThat(s.phase).isEqualTo(ExplorePhase.Loading)
            assertThat(s.items).isEmpty()
            assertThat(s.nextCursor).isNull()
            assertThat(repo.calls.last()).isEqualTo(ListCall("", "cerrado", null, null))
        }

    @Test
    fun `mudar o filtro com uma pagina seguinte em curso descarta essa pagina`() =
        runTest {
            repo.pages["c1"] = Result.Success(page(20, 40, "c2"))
            repo.gates[1] = CompletableDeferred()
            val vm = viewModel()
            vm.onAction(ExploreAction.LoadMore)

            vm.onAction(ExploreAction.OpenFilters)
            vm.onAction(ExploreAction.SheetState("SP"))
            vm.onAction(ExploreAction.ApplyFilters)
            repo.gates.getValue(1).complete(Unit)
            advanceUntilIdle()

            assertThat(vm.state.value.items).hasSize(20)
            assertThat(vm.state.value.nextCursor).isEqualTo("c1")
            assertThat(repo.calls.last()).isEqualTo(ListCall("", null, "SP", null))
        }

    @Test
    fun `shouldLoadMore pede a proxima pagina a 6 itens do fim`() {
        assertThat(shouldLoadMore(lastVisibleIndex = 13, itemCount = 20)).isFalse()
        assertThat(shouldLoadMore(lastVisibleIndex = 14, itemCount = 20)).isTrue()
        assertThat(shouldLoadMore(lastVisibleIndex = -1, itemCount = 0)).isFalse()
    }

    // --- Erros ---

    @Test
    fun `cada erro de rede vira a tela de erro certa`() =
        runTest {
            val table =
                mapOf(
                    DataError.Network.NoConnection to ScreenError.NoConnection,
                    DataError.Network.Timeout to ScreenError.NoConnection,
                    DataError.Network.ServiceUnavailable to ScreenError.ServiceUnavailable,
                    DataError.Network.Internal to ScreenError.ServiceUnavailable,
                    DataError.Network.RateLimited(null) to ScreenError.RateLimited(null),
                    DataError.Network.InvalidParameter to ScreenError.Generic,
                    DataError.Network.NotFound to ScreenError.Generic,
                    DataError.Network.Unknown to ScreenError.Generic,
                )
            for ((error, expected) in table) {
                repo.pages[null] = Result.Failure(error)
                assertThat(viewModel().state.value.phase).isEqualTo(ExplorePhase.Error(expected))
            }
        }

    @Test
    fun `tentar de novo recarrega a primeira pagina`() =
        runTest {
            repo.pages[null] = Result.Failure(DataError.Network.ServiceUnavailable)
            val vm = viewModel()
            repo.pages[null] = Result.Success(page(0, 3, null))

            vm.onAction(ExploreAction.Retry)

            assertThat(vm.state.value.phase).isEqualTo(ExplorePhase.Content)
            assertThat(vm.state.value.items).hasSize(3)
        }

    @Test
    fun `RateLimited so libera tentar de novo depois do Retry-After`() =
        runTest {
            repo.pages[null] = Result.Failure(DataError.Network.RateLimited(30))
            val vm = viewModel()
            assertThat(vm.state.value.retryEnabled).isFalse()

            vm.onAction(ExploreAction.Retry)
            assertThat(repo.calls).hasSize(1)

            advanceTimeBy(30_001)
            assertThat(vm.state.value.retryEnabled).isTrue()
            vm.onAction(ExploreAction.Retry)
            assertThat(repo.calls).hasSize(2)
        }

    // --- Filtros ---

    @Test
    fun `os filtros carregam ao abrir a tela`() =
        runTest {
            val vm = viewModel()
            assertThat(vm.state.value.filters).isEqualTo(FiltersPhase.Loaded(sampleFilters))
        }

    @Test
    fun `o sheet so aplica ao tocar em Mostrar aves`() =
        runTest {
            val vm = viewModel()
            vm.onAction(ExploreAction.OpenFilters)
            vm.onAction(ExploreAction.SheetBiome("pampa"))
            assertThat(vm.state.value.sheet).isEqualTo(FiltersSheetState(biome = "pampa", state = null))
            assertThat(vm.state.value.biome).isNull()

            vm.onAction(ExploreAction.DismissFilters)
            assertThat(vm.state.value.sheet).isNull()
            assertThat(vm.state.value.biome).isNull()
            assertThat(repo.calls).hasSize(1)
        }

    @Test
    fun `selecao unica por grupo e tocar de novo desmarca`() =
        runTest {
            val vm = viewModel()
            vm.onAction(ExploreAction.OpenFilters)
            vm.onAction(ExploreAction.SheetBiome("pampa"))
            vm.onAction(ExploreAction.SheetBiome("cerrado"))
            vm.onAction(ExploreAction.SheetState("SP"))
            assertThat(vm.state.value.sheet).isEqualTo(FiltersSheetState(biome = "cerrado", state = "SP"))

            vm.onAction(ExploreAction.SheetBiome("cerrado"))
            assertThat(
                vm.state.value.sheet
                    ?.biome,
            ).isNull()
        }

    @Test
    fun `Limpar no sheet desmarca tudo mas so vale ao aplicar`() =
        runTest {
            val vm = viewModel(SavedStateHandle(mapOf(KEY_BIOME to "pampa", KEY_STATE to "RS")))
            vm.onAction(ExploreAction.OpenFilters)
            assertThat(vm.state.value.sheet).isEqualTo(FiltersSheetState(biome = "pampa", state = "RS"))

            vm.onAction(ExploreAction.ClearSheet)
            assertThat(vm.state.value.biome).isEqualTo("pampa")

            vm.onAction(ExploreAction.ApplyFilters)
            assertThat(vm.state.value.biome).isNull()
            assertThat(vm.state.value.state).isNull()
            assertThat(repo.calls.last()).isEqualTo(ListCall("", null, null, null))
        }

    @Test
    fun `Limpar filtros do estado vazio mantem a busca`() =
        runTest {
            repo.pages[null] = Result.Success(SpeciesPage(emptyList(), null))
            val vm = viewModel(SavedStateHandle(mapOf(KEY_QUERY to "arara", KEY_BIOME to "pampa")))
            assertThat(vm.state.value.phase).isEqualTo(ExplorePhase.Empty)

            vm.onAction(ExploreAction.ClearFilters)

            assertThat(repo.calls.last()).isEqualTo(ListCall("arara", null, null, null))
            assertThat(vm.state.value.query).isEqualTo("arara")
        }

    @Test
    fun `o X do chip remove so aquele filtro`() =
        runTest {
            val vm = viewModel(SavedStateHandle(mapOf(KEY_BIOME to "pampa", KEY_STATE to "RS")))
            vm.onAction(ExploreAction.ClearBiome)
            assertThat(repo.calls.last()).isEqualTo(ListCall("", null, "RS", null))
            vm.onAction(ExploreAction.ClearState)
            assertThat(repo.calls.last()).isEqualTo(ListCall("", null, null, null))
        }

    @Test
    fun `falha nos filtros mostra erro e abrir o sheet tenta de novo`() =
        runTest {
            filters.next = Result.Failure(DataError.Network.NoConnection)
            val vm = viewModel()
            assertThat(vm.state.value.filters).isEqualTo(FiltersPhase.Error)

            filters.next = Result.Success(sampleFilters)
            vm.onAction(ExploreAction.OpenFilters)

            assertThat(filters.calls).isEqualTo(2)
            assertThat(vm.state.value.filters).isEqualTo(FiltersPhase.Loaded(sampleFilters))
        }

    @Test
    fun `estados ordenados por contagem e os 12 primeiros ate ver todos`() {
        val many = (1..20).map { StateFilter("U$it", it) }
        assertThat(visibleStates(sampleFilters.states, showAll = false).map { it.code }).containsExactly("SP", "RJ", "AC")
        assertThat(visibleStates(many, showAll = false)).hasSize(VISIBLE_STATES)
        assertThat(visibleStates(many, showAll = false).first().code).isEqualTo("U20")
        assertThat(visibleStates(many, showAll = true)).hasSize(20)
    }

    // --- Favoritos, eventos e morte do processo ---

    @Test
    fun `favoritar no card alterna e o estado acompanha`() =
        runTest {
            val vm = viewModel()
            val first =
                vm.state.value.items
                    .first()

            vm.onAction(ExploreAction.ToggleFavorite(first))
            assertThat(vm.state.value.favoriteIds).isEqualTo(setOf(first.id))

            vm.onAction(ExploreAction.ToggleFavorite(first))
            assertThat(vm.state.value.favoriteIds).isEmpty()
        }

    @Test
    fun `tocar no card e em Ver favoritos emite os eventos de navegacao`() =
        runTest {
            val vm = viewModel()
            vm.events.test {
                vm.onAction(ExploreAction.OpenDetail("bem-te-vi"))
                assertThat(awaitItem()).isEqualTo(ExploreEvent.NavigateToDetail("bem-te-vi"))
                vm.onAction(ExploreAction.OpenFavorites)
                assertThat(awaitItem()).isEqualTo(ExploreEvent.NavigateToFavorites)
            }
        }

    @Test
    fun `busca e filtros sobrevivem a morte do processo`() =
        runTest {
            val saved = SavedStateHandle(mapOf(KEY_QUERY to "azul", KEY_BIOME to "cerrado"))
            val vm = viewModel(saved)
            assertThat(repo.calls).containsExactly(ListCall("azul", "cerrado", null, null))

            vm.onAction(ExploreAction.QueryChange("gralha"))
            vm.onAction(ExploreAction.OpenFilters)
            vm.onAction(ExploreAction.SheetState("PR"))
            vm.onAction(ExploreAction.ApplyFilters)

            assertThat(saved.get<String>(KEY_QUERY)).isEqualTo("gralha")
            assertThat(saved.get<String>(KEY_BIOME)).isEqualTo("cerrado")
            assertThat(saved.get<String>(KEY_STATE)).isEqualTo("PR")
        }
}
