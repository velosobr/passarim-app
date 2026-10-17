// O estado da aba Favoritos.

package com.velosobr.passarim.feature.favorites.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

/** Carregando (o banco ainda não respondeu), com itens ou vazio. */
sealed interface FavoritesState {
    data object Loading : FavoritesState

    data class Content(
        val items: List<SpeciesSummary>,
    ) : FavoritesState

    data object Empty : FavoritesState
}
