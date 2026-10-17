package com.velosobr.passarim.feature.favorites.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

sealed interface FavoritesAction {
    data class OpenDetail(
        val id: String,
    ) : FavoritesAction

    data class ToggleFavorite(
        val summary: SpeciesSummary,
    ) : FavoritesAction

    data object ExploreClick : FavoritesAction
}

sealed interface FavoritesEvent {
    data class NavigateToDetail(
        val id: String,
    ) : FavoritesEvent

    data object NavigateToExplore : FavoritesEvent
}
