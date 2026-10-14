package com.velosobr.passarim.app.favorites

import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryFavoritesRepository : FavoritesRepository {
    private val _summaries = MutableStateFlow<Map<String, SpeciesSummary>>(emptyMap())
    val summaries: StateFlow<Map<String, SpeciesSummary>> = _summaries.asStateFlow()

    override fun observeIds(): Flow<Set<String>> = _summaries.map { it.keys }.distinctUntilChanged()

    override suspend fun toggle(summary: SpeciesSummary) {
        _summaries.update { if (summary.id in it) it - summary.id else it + (summary.id to summary) }
    }

    override suspend fun refresh(summary: SpeciesSummary) {
        _summaries.update { if (summary.id in it) it + (summary.id to summary) else it }
    }
}
