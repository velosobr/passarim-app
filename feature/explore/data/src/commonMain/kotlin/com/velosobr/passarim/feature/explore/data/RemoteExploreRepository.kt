// A lista do Explorar vinda do BFF: os DTOs, a conversão para o domínio e o
// repositório.

package com.velosobr.passarim.feature.explore.data

import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.data.safeCall
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.domain.biomeOf
import com.velosobr.passarim.core.domain.map
import com.velosobr.passarim.feature.explore.domain.ExploreRepository
import com.velosobr.passarim.feature.explore.domain.SpeciesPage
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

/**
 * O contrato também traz `conservationStatus`; os cards não o mostram, então o DTO o ignora.
 * `primaryBiome` só existe a partir da frente B do BFF: num BFF antigo ele falta e fica nulo.
 */
@Serializable
internal data class SpeciesSummaryDto(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String? = null,
    val primaryBiome: String? = null,
)

/** Uma página da resposta; nextCursor ausente vira null (última página). */
@Serializable
internal data class SpeciesPageDto(
    val items: List<SpeciesSummaryDto>,
    val nextCursor: String? = null,
)

/**
 * Converte o resumo no domínio, trocando o host da miniatura em debug. biomeOf devolve null para um
 * bioma que o app não conhece (um bioma novo no BFF não derruba a lista).
 */
internal fun SpeciesSummaryDto.toDomain(rewriter: MediaUrlRewriter) =
    SpeciesSummary(id, commonName, scientificName, rewriter.rewrite(thumbnailUrl), biomeOf(primaryBiome))

/** Converte a página no domínio. */
internal fun SpeciesPageDto.toDomain(rewriter: MediaUrlRewriter) =
    SpeciesPage(
        items = items.map { it.toDomain(rewriter) },
        nextCursor = nextCursor,
    )

/**
 * `GET /v1/species`. Parâmetros nulos ficam fora da URL: o `parameter` do Ktor ignora null (o BFF recusa `biome=` vazio).
 * A busca nunca é nula, por isso é a única que precisa do if para ficar fora quando está em branco.
 *
 * No Kids vão também `audience=kids` e, na tela de mundo, `primaryBiome=<código>`; o adulto não manda nenhum
 * dos dois, e a URL dele fica como sempre foi.
 *
 * Conferência da resposta do Kids: toda ave com conteúdo Kids tem bioma principal (o catalog grava os dois
 * juntos), então um item sem ele (ou com um bioma que o app não conhece) quer dizer um BFF que ignorou o
 * `audience` e mandou a lista do adulto. Esse item é descartado: sem mundo, ele não teria cor nem lugar no
 * álbum. O cursor segue o do BFF, e a próxima página continua de onde a lista parou.
 */
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
        audience: Journey,
        primaryBiome: Biome?,
    ): Result<SpeciesPage, DataError.Network> =
        safeCall<SpeciesPageDto> {
            client.get("/v1/species") {
                if (query.isNotBlank()) parameter("q", query)
                parameter("biome", biome)
                parameter("state", state)
                parameter("cursor", cursor)
                parameter("limit", limit)
                if (audience == Journey.KIDS) parameter("audience", "kids")
                parameter("primaryBiome", primaryBiome?.code)
            }
        }.map { dto ->
            val page = dto.toDomain(rewriter)
            if (audience == Journey.KIDS) page.copy(items = page.items.filter { it.primaryBiome != null }) else page
        }
}
