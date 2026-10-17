// As opções de filtro vindas do BFF, guardadas em memória depois da primeira
// resposta.

package com.velosobr.passarim.feature.explore.data

import com.velosobr.passarim.core.data.safeCall
import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.StateFilter
import com.velosobr.passarim.core.domain.map
import com.velosobr.passarim.core.domain.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

/** O corpo de `GET /v1/filters`: biomas e UFs com contagem. */
@Serializable
internal data class FiltersDto(
    val biomes: List<BiomeDto>,
    val states: List<StateDto>,
)

/** Um bioma com o rótulo em português. */
@Serializable
internal data class BiomeDto(
    val code: String,
    val label: String,
    val speciesCount: Int,
)

/** Uma UF. */
@Serializable
internal data class StateDto(
    val code: String,
    val speciesCount: Int,
)

/** Converte os DTOs no domínio. */
internal fun FiltersDto.toDomain() =
    Filters(
        biomes = biomes.map { BiomeFilter(it.code, it.label, it.speciesCount) },
        states = states.map { StateFilter(it.code, it.speciesCount) },
    )

/**
 * `GET /v1/filters`: biomas e estados com a contagem de espécies. As contagens mudam pouco, então o
 * primeiro sucesso fica em memória até o processo morrer; uma falha não fica, e a próxima chamada tenta de novo.
 */
class RemoteFiltersRepository(
    private val client: HttpClient,
) : FiltersRepository {
    // O Mutex (trava das coroutines) garante que duas chamadas simultâneas não
    // façam dois pedidos: a segunda espera a primeira e já encontra o cache.
    private val mutex = Mutex()
    private var cached: Filters? = null

    override suspend fun get(): Result<Filters, DataError.Network> =
        mutex.withLock {
            cached?.let { return@withLock Result.Success(it) }
            safeCall<FiltersDto> { client.get("/v1/filters") }
                .map { it.toDomain() }
                .onSuccess { cached = it }
        }
}
