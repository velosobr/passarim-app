// O estado da aba Favoritos.

package com.velosobr.passarim.feature.favorites.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

/** Carregando (o banco ainda não respondeu), com itens ou vazio. */
sealed interface FavoritesState {
    data object Loading : FavoritesState

    /**
     * As aves guardadas, na ordem do repositório (a mais recente primeiro).
     * [count] é calculada a partir de [items], e não guardada à parte: assim
     * as duas nunca discordam. "get() =" refaz a conta a cada leitura, o que
     * é barato (o tamanho de uma lista).
     */
    data class Content(
        val items: List<SpeciesSummary>,
    ) : FavoritesState {
        /** Quantas aves estão guardadas, para o subtítulo ("4 aves guardadas"). */
        val count: Int get() = items.size
    }

    data object Empty : FavoritesState
}
