// Opções de filtro do Explorar e o contrato para buscá-las.

package com.velosobr.passarim.core.domain

/** Um bioma com o rótulo em português e quantas espécies tem. */
data class BiomeFilter(
    val code: String,
    val label: String,
    val speciesCount: Int,
)

/** Uma UF com quantas espécies tem. */
data class StateFilter(
    val code: String,
    val speciesCount: Int,
)

/** Todas as opções de filtro. */
data class Filters(
    val biomes: List<BiomeFilter>,
    val states: List<StateFilter>,
)

/** Busca as opções de filtro no BFF. */
interface FiltersRepository {
    suspend fun get(): Result<Filters, DataError.Network>
}
