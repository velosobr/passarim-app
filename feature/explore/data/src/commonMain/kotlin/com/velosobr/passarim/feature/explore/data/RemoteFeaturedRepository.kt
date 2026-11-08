// A "Ave do dia" vinda do BFF (GET /v1/featured): o DTO, a conversão para o
// domínio e o repositório. Sem cache aqui: o BFF manda Cache-Control até a
// meia-noite de São Paulo.

package com.velosobr.passarim.feature.explore.data

import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.data.safeCall
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.FeaturedRepository
import com.velosobr.passarim.core.domain.FeaturedSpecies
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * O corpo de `GET /v1/featured`. O card só precisa saber se há canto, então o áudio fica como um
 * JsonObject qualquer (nulo quando a ave não tem canto).
 */
@Serializable
internal data class FeaturedDto(
    val date: String,
    val species: SpeciesSummaryDto,
    val audio: JsonObject? = null,
)

/** `GET /v1/featured`: a ave do dia, com o resumo convertido como o da lista. */
class RemoteFeaturedRepository(
    private val client: HttpClient,
    private val rewriter: MediaUrlRewriter,
) : FeaturedRepository {
    override suspend fun getFeaturedSpecies(): Result<FeaturedSpecies, DataError.Network> =
        safeCall<FeaturedDto> { client.get("/v1/featured") }
            .map { FeaturedSpecies(date = it.date, species = it.species.toDomain(rewriter), hasSong = it.audio != null) }
}
