// A checagem do Passarim Kids no BFF. Mora no feature:explore:data porque
// reaproveita o HttpClient e o FiltersDto do /v1/filters (no
// RemoteFiltersRepository.kt), em vez de criar um módulo core só para isso.

package com.velosobr.passarim.feature.explore.data

import com.velosobr.passarim.core.data.safeCall
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.KidsAvailabilityRepository
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * `GET /v1/filters?audience=kids`: o Kids está disponível quando a soma das
 * contagens por bioma principal (`primaryBiomes`) passa de zero.
 *
 * - Um BFF de antes da frente B ignora o `audience` e não manda
 *   `primaryBiomes`; o FiltersDto lê o campo ausente como lista vazia, a soma
 *   dá zero, e o Kids fica escondido.
 * - Um catálogo sem aves para o Kids manda as contagens zeradas: também
 *   escondido, para o Kids não abrir vazio.
 *
 * De propósito, não passa pelo cache em memória do RemoteFiltersRepository:
 * aquele cache guarda os filtros do adulto (sem `audience`), e a resposta
 * daqui tem de ser a de agora, a cada abertura da Escolha ou das Configurações.
 */
class RemoteKidsAvailabilityRepository(
    private val client: HttpClient,
) : KidsAvailabilityRepository {
    override suspend fun checkKidsAvailability(): Result<Boolean, DataError.Network> =
        safeCall<FiltersDto> { client.get("/v1/filters") { parameter("audience", "kids") } }
            .map { filters -> filters.primaryBiomes.sumOf { it.speciesCount } > 0 }
}
