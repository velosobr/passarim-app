package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

/**
 * Favoritos do usuário, usados por Explorar, Detalhe e Favoritos. Na 5b o `app` registra uma
 * implementação em memória; a 5d a troca por uma com Room, sem mudar as features.
 */
interface FavoritesRepository {
    /** Ids favoritados; emite de novo a cada mudança. */
    fun observeIds(): Flow<Set<String>>

    /** Favorita a espécie se ela não for favorita; senão, desfavorita. */
    suspend fun toggle(summary: SpeciesSummary)

    suspend fun refresh(summary: SpeciesSummary)
}
