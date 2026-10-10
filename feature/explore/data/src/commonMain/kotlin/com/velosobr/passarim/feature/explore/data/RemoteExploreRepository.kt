package com.velosobr.passarim.feature.explore.data

import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.data.safeCall
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.domain.map
import com.velosobr.passarim.feature.explore.domain.ExploreRepository
import com.velosobr.passarim.feature.explore.domain.SpeciesPage
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

/** O contrato também traz `conservationStatus`; os cards não o mostram, então o DTO o ignora. */
@Serializable
internal data class SpeciesSummaryDto(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String? = null,
)

@Serializable
internal data class SpeciesPageDto(
    val items: List<SpeciesSummaryDto>,
    val nextCursor: String? = null,
)

internal fun SpeciesPageDto.toDomain(rewriter: MediaUrlRewriter) =
    SpeciesPage(
        items = items.map { SpeciesSummary(it.id, it.commonName, it.scientificName, rewriter.rewrite(it.thumbnailUrl)) },
        nextCursor = nextCursor,
    )

/** `GET /v1/species`. Parâmetros vazios ou nulos ficam fora da URL (o BFF recusa `biome=` vazio). */
class RemoteExploreRepository(
    private val client: HttpClient,
    private val rewriter: MediaUrlRewriter,
) : ExploreRepository {
    override suspend fun list(
        query: String,
        biome: String?,
        state: String?,
        cursor: String?,
        limit: Int,
    ): Result<SpeciesPage, DataError.Network> =
        safeCall<SpeciesPageDto> {
            client.get("/v1/species") {
                if (query.isNotBlank()) parameter("q", query)
                parameter("biome", biome)
                parameter("state", state)
                parameter("cursor", cursor)
                parameter("limit", limit)
            }
        }.map { it.toDomain(rewriter) }
}
