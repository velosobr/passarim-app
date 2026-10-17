package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeIds(): Flow<Set<String>>

    fun observeAll(): Flow<List<SpeciesSummary>>

    suspend fun toggle(summary: SpeciesSummary)

    suspend fun refresh(summary: SpeciesSummary)
}
