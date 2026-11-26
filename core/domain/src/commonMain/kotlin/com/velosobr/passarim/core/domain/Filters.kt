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

/**
 * Busca as opções de filtro no BFF. [audience] diz de qual jornada: ADULT (o padrão) traz as contagens
 * do catálogo todo; KIDS traz só as das aves com conteúdo para crianças, e o primaryBiomes dele é a
 * contagem de aves de cada mundo do Kids.
 */
interface FiltersRepository {
    suspend fun getFilters(audience: Journey = Journey.ADULT): Result<Filters, DataError.Network>
}
