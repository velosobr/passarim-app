package com.velosobr.passarim.feature.explore.domain

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.SpeciesSummary

/** Tamanho fixo da página pedida ao BFF. */
const val PAGE_SIZE = 20

/** Uma página da lista. [nextCursor] nulo quer dizer que não há mais páginas. */
data class SpeciesPage(
    val items: List<SpeciesSummary>,
    val nextCursor: String?,
)

/** `GET /v1/species`: busca por nome e filtros de bioma e estado, paginada por cursor. */
interface ExploreRepository {
    suspend fun list(
        query: String,
        biome: String?,
        state: String?,
        cursor: String?,
        limit: Int = PAGE_SIZE,
    ): Result<SpeciesPage, DataError.Network>
}
