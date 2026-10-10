package com.velosobr.passarim.feature.explore.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

sealed interface ExploreAction {
    data class QueryChange(
        val text: String,
    ) : ExploreAction

    /** Chegou perto do fim da grade (veja [shouldLoadMore]). */
    data object LoadMore : ExploreAction

    data object RetryLoadMore : ExploreAction

    data object Retry : ExploreAction

    /** "Limpar filtros" do estado vazio: tira bioma e estado, mantém a busca. */
    data object ClearFilters : ExploreAction

    /** O X do chip selecionado. */
    data object ClearBiome : ExploreAction

    data object ClearState : ExploreAction

    data object OpenFilters : ExploreAction

    data object DismissFilters : ExploreAction

    /** Seleciona o bioma no sheet; tocar no já selecionado desmarca. */
    data class SheetBiome(
        val code: String,
    ) : ExploreAction

    data class SheetState(
        val code: String,
    ) : ExploreAction

    data object ShowAllStates : ExploreAction

    /** "Limpar" do sheet: desmarca tudo no sheet. */
    data object ClearSheet : ExploreAction

    /** "Mostrar aves". */
    data object ApplyFilters : ExploreAction

    data object RetryFilters : ExploreAction

    data class ToggleFavorite(
        val summary: SpeciesSummary,
    ) : ExploreAction

    data class OpenDetail(
        val id: String,
    ) : ExploreAction

    data object OpenFavorites : ExploreAction
}

sealed interface ExploreEvent {
    data class NavigateToDetail(
        val id: String,
    ) : ExploreEvent

    data object NavigateToFavorites : ExploreEvent
}
