// Testes do safeCall com o MockEngine do Ktor, que responde no lugar do BFF
// sem rede. runTest (das coroutines) roda funções suspend dentro do teste.

package com.velosobr.passarim.core.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test

/** Cada tipo de resposta e de falha vira o Result esperado. */
class SafeCallTest {
    /** Pong é um DTO mínimo para testar a leitura do corpo. */
    @Serializable
    private data class Pong(
        val message: String,
    )

    /** client cria um HttpClient cujo "servidor" é a função handler. */
    private fun client(handler: MockRequestHandler) = HttpClient(MockEngine(handler)) { install(ContentNegotiation) { json(bffJson) } }

    private suspend fun call(handler: MockRequestHandler): Result<Pong, DataError.Network> =
        client(handler).let { c -> safeCall<Pong> { c.get("http://localhost/ping") } }

    /** problem responde um erro em problem+json com o code dado. */
    private fun problem(
        code: String,
        status: HttpStatusCode,
        extra: Map<String, String> = emptyMap(),
    ): MockRequestHandler =
        {
            respond(
                content = """{"type":"urn:x","title":"t","status":${status.value},"code":"$code","requestId":"r"}""",
                status = status,
                headers =
                    headersOf(
                        HttpHeaders.ContentType to listOf("application/problem+json"),
                        *extra.map { it.key to listOf(it.value) }.toTypedArray(),
                    ),
            )
        }

    /** plain responde um erro em texto puro, como um proxy faria. */
    private fun plain(
        status: HttpStatusCode,
        body: String = "",
    ): MockRequestHandler =
        {
            respond(content = body, status = status, headers = headersOf(HttpHeaders.ContentType, "text/plain"))
        }

    @Test
    fun `sucesso devolve o corpo decodificado`() =
        runTest {
            val result =
                call {
                    respond("""{"message":"oi","extra":1}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
                }
            assertThat(result).isEqualTo(Result.Success(Pong("oi")))
        }

    @Test
    fun `cada code do problem+json vira o erro certo`() =
        runTest {
            val table =
                mapOf(
                    "SPECIES_NOT_FOUND" to DataError.Network.NotFound,
                    "NOT_FOUND" to DataError.Network.NotFound,
                    "INVALID_PARAMETER" to DataError.Network.InvalidParameter,
                    "METHOD_NOT_ALLOWED" to DataError.Network.InvalidParameter,
                    "SERVICE_UNAVAILABLE" to DataError.Network.ServiceUnavailable,
                    "INTERNAL" to DataError.Network.Internal,
                    "ALGO_NOVO" to DataError.Network.Unknown,
                )
            for ((code, expected) in table) {
                assertThat(call(problem(code, HttpStatusCode.BadRequest))).isEqualTo(Result.Failure(expected))
            }
        }

    @Test
    fun `RATE_LIMITED le o Retry-After`() =
        runTest {
            val r = call(problem("RATE_LIMITED", HttpStatusCode.TooManyRequests, mapOf("Retry-After" to "30")))
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.RateLimited(30)))
        }

    @Test
    fun `RATE_LIMITED sem Retry-After valido fica nulo`() =
        runTest {
            val r = call(problem("RATE_LIMITED", HttpStatusCode.TooManyRequests, mapOf("Retry-After" to "amanha")))
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.RateLimited(null)))
        }

    @Test
    fun `404 em texto puro do proxy nunca vira NotFound`() =
        runTest {
            val r = call(plain(HttpStatusCode.NotFound, "404 page not found"))
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.ServiceUnavailable))
        }

    @Test
    fun `status de proxy sem problem+json`() =
        runTest {
            val table =
                mapOf(
                    HttpStatusCode.BadGateway to DataError.Network.ServiceUnavailable,
                    HttpStatusCode.ServiceUnavailable to DataError.Network.ServiceUnavailable,
                    HttpStatusCode.GatewayTimeout to DataError.Network.ServiceUnavailable,
                    HttpStatusCode.InternalServerError to DataError.Network.Internal,
                    HttpStatusCode.BadRequest to DataError.Network.Unknown,
                )
            for ((status, expected) in table) {
                assertThat(call(plain(status))).isEqualTo(Result.Failure(expected))
            }
        }

    @Test
    fun `problem+json com corpo invalido cai na classificacao por status`() =
        runTest {
            val r =
                call {
                    respond("nao e json", HttpStatusCode.ServiceUnavailable, headersOf(HttpHeaders.ContentType, "application/problem+json"))
                }
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.ServiceUnavailable))
        }

    @Test
    fun `timeout do Ktor vira Timeout`() =
        runTest {
            val r = call { throw HttpRequestTimeoutException("http://localhost/ping", 1_000) }
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.Timeout))
        }

    @Test
    fun `falha de transporte vira NoConnection`() =
        runTest {
            val r = call { throw IllegalStateException("sem rede") }
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.NoConnection))
        }

    @Test
    fun `200 com corpo invalido vira Unknown`() =
        runTest {
            val r =
                call {
                    respond("""{"outra":"coisa"}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
                }
            assertThat(r).isEqualTo(Result.Failure(DataError.Network.Unknown))
        }

    @Test
    fun `corpo de sucesso pode ser lido com body direto`() =
        runTest {
            val c = client { respond("""{"message":"x"}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json")) }
            assertThat(c.get("http://localhost/ping").body<Pong>()).isEqualTo(Pong("x"))
        }
}
