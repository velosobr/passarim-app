// O estado da tela Explorar e as fases da lista e dos filtros.

package com.velosobr.passarim.feature.explore.presentation

import com.velosobr.passarim.core.domain.FeaturedSpecies
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.domain.StateFilter
import com.velosobr.passarim.core.presentation.ScreenError

/** Quantos estados o sheet mostra antes de "Ver todos os estados". */
const val VISIBLE_STATES = 12

/** Estado da tela Explorar. Os critérios (`query`, `biome`, `state`) definem a lista; o resto deriva deles. */
data class ExploreState(
    /** Texto do campo, como o usuário digitou (a busca usa a versão limpa por `sanitizeQuery`). */
    val query: String = "",
    val biome: String? = null,
    val state: String? = null,
    val items: List<SpeciesSummary> = emptyList(),
    val nextCursor: String? = null,
    val phase: ExplorePhase = ExplorePhase.Loading,
    val isLoadingMore: Boolean = false,
    /** Erro da página seguinte: uma linha no fim da grade, sem perder os itens. */
    val loadMoreError: ScreenError? = null,
    /** Falso enquanto corre o `Retry-After` de um `RateLimited`. */
    val retryEnabled: Boolean = true,
    val filters: FiltersPhase = FiltersPhase.Loading,
    /** Sheet de filtros aberto (com as escolhas ainda não aplicadas) ou nulo, se fechado. */
    val sheet: FiltersSheetState? = null,
    val favoriteIds: Set<String> = emptySet(),
    /** A "Ave do dia", ou nulo enquanto carrega ou se o BFF não tiver uma (aí o card não aparece). */
    val featured: FeaturedSpecies? = null,
    /** A "Ave do dia" está a caminho: a grade já reserva o lugar dela no topo. */
    val featuredLoading: Boolean = false,
) {
    /** Nome do bioma selecionado para o chip ("Pampa"); o código, se os filtros ainda não chegaram. */
    val biomeLabel: String?
        get() =
            biome?.let { code ->
                (filters as? FiltersPhase.Loaded)
                    ?.filters
                    ?.biomes
                    ?.find { it.code == code }
                    ?.label ?: code
            }

    /** Se há bioma ou UF selecionados. */
    val hasFilters: Boolean get() = biome != null || state != null

    /** A "Ave do dia" só aparece na vitrine: sem busca e sem filtro. */
    val showFeatured: Boolean get() = featured != null && query.isBlank() && !hasFilters

    /**
     * O lugar da "Ave do dia" no topo da grade: existe enquanto ela carrega e depois que ela chega.
     * Reservar o lugar desde o começo evita que o card entre acima de uma grade já desenhada; as
     * grades guardam a posição pelo primeiro item visível, e o card nasceria fora da tela.
     */
    val showFeaturedSlot: Boolean get() = (featuredLoading || featured != null) && query.isBlank() && !hasFilters

    /** O total de aves para o título ("41 aves"), ou nulo enquanto os filtros não chegam. */
    val speciesTotal: Int? get() = (filters as? FiltersPhase.Loaded)?.filters?.speciesTotal
}

/** Em que ponto a lista está: carregando, com itens, vazia ou com erro. */
sealed interface ExplorePhase {
    data object Loading : ExplorePhase

    data object Content : ExplorePhase

    data object Empty : ExplorePhase

    data class Error(
        val error: ScreenError,
    ) : ExplorePhase
}

/** Os filtros têm carregamento próprio: a grade funciona mesmo se `GET /v1/filters` falhar. */
sealed interface FiltersPhase {
    data object Loading : FiltersPhase

    data class Loaded(
        val filters: Filters,
    ) : FiltersPhase

    data object Error : FiltersPhase
}

/** Escolhas no sheet; só viram filtro ao tocar em "Mostrar aves". */
data class FiltersSheetState(
    val biome: String?,
    val state: String?,
    val showAllStates: Boolean = false,
)

/** Estados por contagem (maior primeiro); só os [VISIBLE_STATES] primeiros até "Ver todos". */
fun visibleStates(
    states: List<StateFilter>,
    showAll: Boolean,
): List<StateFilter> = states.sortedByDescending { it.speciesCount }.let { if (showAll) it else it.take(VISIBLE_STATES) }
