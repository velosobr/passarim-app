package com.velosobr.passarim.feature.favorites.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

sealed interface FavoritesState {
    data object Loading : FavoritesState

    data class Content(
        val items: List<SpeciesSummary>,
    ) : FavoritesState

    data object Empty : FavoritesState
}
