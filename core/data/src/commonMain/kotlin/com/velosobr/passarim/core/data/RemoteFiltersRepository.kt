package com.velosobr.passarim.core.data

import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.StateFilter
import com.velosobr.passarim.core.domain.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
internal data class FiltersDto(
    val biomes: List<BiomeDto>,
    val states: List<StateDto>,
)

@Serializable
internal data class BiomeDto(
    val code: String,
    val label: String,
    val speciesCount: Int,
)

@Serializable
internal data class StateDto(
    val code: String,
    val speciesCount: Int,
)

internal fun FiltersDto.toDomain() =
    Filters(
        biomes = biomes.map { BiomeFilter(it.code, it.label, it.speciesCount) },
        states = states.map { StateFilter(it.code, it.speciesCount) },
    )

/** `GET /v1/filters`: biomas e estados com a contagem de espécies. */
class RemoteFiltersRepository(
    private val client: HttpClient,
) : FiltersRepository {
    override suspend fun get(): Result<Filters, DataError.Network> =
        safeCall<FiltersDto> { client.get("/v1/filters") }.map { it.toDomain() }
}
