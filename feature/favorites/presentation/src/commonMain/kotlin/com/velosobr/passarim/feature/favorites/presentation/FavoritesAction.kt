// As ações da aba Favoritos e os eventos de navegação.

package com.velosobr.passarim.feature.favorites.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

/** O que o usuário pode fazer na aba: abrir uma ave, desfavoritar ou ir ao Explorar. */
sealed interface FavoritesAction {
    data class OpenDetail(
        val id: String,
    ) : FavoritesAction

    data class ToggleFavorite(
        val summary: SpeciesSummary,
    ) : FavoritesAction

    data object ExploreClick : FavoritesAction
}

/** Eventos de uma vez: abrir o Detalhe ou a aba Explorar. */
sealed interface FavoritesEvent {
    data class NavigateToDetail(
        val id: String,
    ) : FavoritesEvent

    data object NavigateToExplore : FavoritesEvent
}
