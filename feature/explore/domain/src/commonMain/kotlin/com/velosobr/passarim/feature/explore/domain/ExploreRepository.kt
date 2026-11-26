// Contrato da lista do Explorar e o tamanho da página.

package com.velosobr.passarim.feature.explore.domain

import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.SpeciesSummary

/** Tamanho fixo da página pedida ao BFF. */
const val PAGE_SIZE = 20

/** Uma página da lista. [nextCursor] nulo quer dizer que não há mais páginas. */
data class SpeciesPage(
    val items: List<SpeciesSummary>,
    val nextCursor: String?,
)

/**
 * `GET /v1/species`: busca por nome e filtros de bioma e estado, paginada por cursor.
 *
 * [audience] diz de qual jornada é a lista: o padrão ADULT é o Explorar de sempre, e KIDS pede só as
 * aves com conteúdo para crianças. [primaryBiome] filtra pelo bioma principal, o "mundo" do Kids
 * (a tela de mundo); nulo, a lista vem de todos os mundos. Os dois têm valor padrão para o adulto
 * continuar chamando como antes.
 */
interface ExploreRepository {
    suspend fun list(
        query: String,
        biome: String?,
        state: String?,
        cursor: String?,
        limit: Int = PAGE_SIZE,
        audience: Journey = Journey.ADULT,
        primaryBiome: Biome? = null,
    ): Result<SpeciesPage, DataError.Network>
}
