// Testes da checagem do Passarim Kids: o pedido feito ao BFF, a regra da soma
// das contagens por bioma principal e os erros de rede. O MockEngine do Ktor
// faz o papel do BFF: cada teste diz o que ele responde, sem rede de verdade.
// runTest roda o corpo do teste numa coroutine de teste, o que permite chamar
// a função suspend checkKidsAvailability() direto, sem esperar tempo real.

package com.velosobr.passarim.feature.explore.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.data.createHttpClient
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Os corpos abaixo imitam o BFF com e sem a frente B. json é uma extension
 * function de MockRequestHandleScope (o "this" dentro do bloco do
 * MockEngine): responde 200 com o corpo dado e o Content-Type de JSON.
 */
class RemoteKidsAvailabilityRepositoryTest {
    private val config = AppConfig(isDebug = false, baseUrl = "http://localhost:8080", mediaHostOverride = null, versionName = "1.0.0")

    private val withKids =
        """
        {"biomes":[],"states":[],
         "primaryBiomes":[{"code":"cerrado","label":"Cerrado","speciesCount":11},{"code":"pampa","label":"Pampa","speciesCount":2}]}
        """.trimIndent()

    // O BFF de antes da frente B ignora o ?audience=kids e não manda primaryBiomes.
    private val oldBff =
        """
        {"biomes":[{"code":"cerrado","label":"Cerrado","speciesCount":26}],"states":[{"code":"SP","speciesCount":29}]}
        """.trimIndent()

    private val noKidsBirds =
        """
        {"biomes":[],"states":[],
         "primaryBiomes":[{"code":"cerrado","label":"Cerrado","speciesCount":0},{"code":"pampa","label":"Pampa","speciesCount":0}]}
        """.trimIndent()

    private fun MockRequestHandleScope.json(body: String) =
        respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun repository(engine: MockEngine) = RemoteKidsAvailabilityRepository(createHttpClient(engine, config))

    @Test
    fun `pede GET v1 filters com audience kids`() =
        runTest {
            var seenUrl = ""
            var seenMethod: HttpMethod? = null
            val engine =
                MockEngine { request ->
                    seenUrl = request.url.toString()
                    seenMethod = request.method
                    json(withKids)
                }

            repository(engine).checkKidsAvailability()

            assertThat(seenUrl).isEqualTo("http://localhost:8080/v1/filters?audience=kids")
            assertThat(seenMethod).isEqualTo(HttpMethod.Get)
        }

    @Test
    fun `disponivel quando a soma por bioma principal passa de zero`() =
        runTest {
            val result = repository(MockEngine { json(withKids) }).checkKidsAvailability()

            assertThat(result).isEqualTo(Result.Success(true))
        }

    @Test
    fun `indisponivel com um BFF sem primaryBiomes`() =
        runTest {
            val result = repository(MockEngine { json(oldBff) }).checkKidsAvailability()

            assertThat(result).isEqualTo(Result.Success(false))
        }

    @Test
    fun `indisponivel quando a soma e zero`() =
        runTest {
            val result = repository(MockEngine { json(noKidsBirds) }).checkKidsAvailability()

            assertThat(result).isEqualTo(Result.Success(false))
        }

    @Test
    fun `erro do servidor vira DataError Network`() =
        runTest {
            val engine = MockEngine { respond("", HttpStatusCode.ServiceUnavailable, headersOf(HttpHeaders.ContentType, "text/plain")) }

            val result = repository(engine).checkKidsAvailability()

            assertThat(result).isEqualTo(Result.Failure(DataError.Network.ServiceUnavailable))
        }

    // Um BFF que não aceita o audience=kids responde 400 em problem+json com o
    // code INVALID_PARAMETER. Para o app, isso é um BFF sem Kids: tentar de
    // novo não resolveria, então o Kids fica escondido em vez de virar erro.
    @Test
    fun `audience recusado pelo BFF esconde o Kids`() =
        runTest {
            val engine =
                MockEngine {
                    respond(
                        """{"status":400,"code":"INVALID_PARAMETER","errors":[{"field":"audience","reason":"use adult ou kids"}]}""",
                        HttpStatusCode.BadRequest,
                        headersOf(HttpHeaders.ContentType, "application/problem+json"),
                    )
                }

            val result = repository(engine).checkKidsAvailability()

            assertThat(result).isEqualTo(Result.Success(false))
        }

    @Test
    fun `sem rede vira NoConnection`() =
        runTest {
            val result = repository(MockEngine { error("sem rede") }).checkKidsAvailability()

            assertThat(result).isEqualTo(Result.Failure(DataError.Network.NoConnection))
        }

    @Test
    fun `cada checagem vai ao BFF sem cache`() =
        runTest {
            var calls = 0
            val repo =
                repository(
                    MockEngine {
                        calls++
                        json(withKids)
                    },
                )

            repo.checkKidsAvailability()
            repo.checkKidsAvailability()

            assertThat(calls).isEqualTo(2)
        }
}
