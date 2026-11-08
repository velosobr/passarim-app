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

/**
 * Todas as opções de filtro. [primaryBiomes] conta as aves pelo bioma principal; como cada ave tem
 * um só bioma principal, a soma dessas contagens é o total de aves ([speciesTotal]). Um BFF de antes
 * da frente B não manda esse campo, e a lista fica vazia.
 */
data class Filters(
    val biomes: List<BiomeFilter>,
    val states: List<StateFilter>,
    val primaryBiomes: List<BiomeFilter> = emptyList(),
) {
    /** O total de aves do catálogo, ou null quando não dá para saber (sem contagem ou com tudo zerado). */
    val speciesTotal: Int?
        get() = primaryBiomes.sumOf { it.speciesCount }.takeIf { it > 0 }
}

/** Busca as opções de filtro no BFF. */
interface FiltersRepository {
    suspend fun get(): Result<Filters, DataError.Network>
}
