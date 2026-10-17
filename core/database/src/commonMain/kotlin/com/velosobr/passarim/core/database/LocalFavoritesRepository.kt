package com.velosobr.passarim.core.database

import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class LocalFavoritesRepository(
    private val dao: FavoriteDao,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : FavoritesRepository {
    override fun observeIds(): Flow<Set<String>> = dao.observeIds().map { it.toSet() }.distinctUntilChanged()

    override fun observeAll(): Flow<List<SpeciesSummary>> = dao.observeAll().map { favorites -> favorites.map { it.toSummary() } }

    override suspend fun toggle(summary: SpeciesSummary) = dao.toggle(summary.toEntity(addedAt = now()))

    override suspend fun refresh(summary: SpeciesSummary) =
        dao.updateSummary(summary.id, summary.commonName, summary.scientificName, summary.thumbnailUrl)
}
