// Implementação do FavoritesRepository do domínio sobre o Room.

package com.velosobr.passarim.core.database

import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/**
 * Favoritos guardados no banco local. [now] é o relógio, trocado nos testes.
 * A audiência vai para o banco como o nome da Journey ("ADULT" ou "KIDS").
 * distinctUntilChanged evita emitir o mesmo conjunto de ids duas vezes
 * seguidas (o Room avisa a cada escrita na tabela, mesmo quando a escrita foi
 * na outra audiência e os ids desta não mudaram).
 */
class LocalFavoritesRepository(
    private val dao: FavoriteDao,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : FavoritesRepository {
    override fun observeIds(audience: Journey): Flow<Set<String>> = dao.observeIds(audience.name).map { it.toSet() }.distinctUntilChanged()

    override fun observeAll(audience: Journey): Flow<List<SpeciesSummary>> =
        dao.observeAll(audience.name).map { favorites -> favorites.map { it.toSummary() } }

    override suspend fun toggle(
        summary: SpeciesSummary,
        audience: Journey,
    ) = dao.toggle(summary.toEntity(addedAt = now(), audience = audience))

    override suspend fun refresh(
        summary: SpeciesSummary,
        audience: Journey,
    ) = dao.updateSummary(
        id = summary.id,
        audience = audience.name,
        commonName = summary.commonName,
        scientificName = summary.scientificName,
        thumbnailUrl = summary.thumbnailUrl,
        primaryBiome = summary.primaryBiome?.code,
    )
}
