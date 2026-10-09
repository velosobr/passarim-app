package com.velosobr.passarim.core.domain

data class BiomeFilter(
    val code: String,
    val label: String,
    val speciesCount: Int,
)

data class StateFilter(
    val code: String,
    val speciesCount: Int,
)

data class Filters(
    val biomes: List<BiomeFilter>,
    val states: List<StateFilter>,
)

interface FiltersRepository {
    suspend fun get(): Result<Filters, DataError.Network>
}
