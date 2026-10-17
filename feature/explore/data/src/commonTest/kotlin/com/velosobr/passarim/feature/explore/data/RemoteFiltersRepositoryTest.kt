// Testes do repositório de filtros: a leitura do JSON e o cache em memória.

package com.velosobr.passarim.feature.explore.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.data.createHttpClient
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.StateFilter
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/** ok responde os filtros e down um 503, para os testes do cache. */
class RemoteFiltersRepositoryTest {
    private val config = AppConfig(isDebug = false, baseUrl = "http://localhost:8080", mediaHostOverride = null, versionName = "1.0.0")

    private val body =
        """
        {"biomes":[{"code":"cerrado","label":"Cerrado","speciesCount":26}],
         "states":[{"code":"SP","speciesCount":29}]}
        """.trimIndent()

    private fun MockRequestHandleScope.ok() = respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun MockRequestHandleScope.down() =
        respond("", HttpStatusCode.ServiceUnavailable, headersOf(HttpHeaders.ContentType, "text/plain"))

    private val expected = Filters(listOf(BiomeFilter("cerrado", "Cerrado", 26)), listOf(StateFilter("SP", 29)))

    @Test
    fun `busca GET v1 filters na base configurada e mapeia para o dominio`() =
        runTest {
            var seenUrl = ""
            var seenMethod: HttpMethod? = null
            val engine =
                MockEngine { request ->
                    seenUrl = request.url.toString()
                    seenMethod = request.method
                    ok()
                }
            val result = RemoteFiltersRepository(createHttpClient(engine, config)).get()

            assertThat(seenUrl).isEqualTo("http://localhost:8080/v1/filters")
            assertThat(seenMethod).isEqualTo(HttpMethod.Get)
            assertThat(result).isEqualTo(Result.Success(expected))
        }

    @Test
    fun `propaga o erro de servidor`() =
        runTest {
            val result = RemoteFiltersRepository(createHttpClient(MockEngine { down() }, config)).get()
            assertThat(result).isEqualTo(Result.Failure(DataError.Network.ServiceUnavailable))
        }

    @Test
    fun `o sucesso fica em memoria e a segunda chamada nao vai a rede`() =
        runTest {
            var calls = 0
            val repo =
                RemoteFiltersRepository(
                    createHttpClient(
                        MockEngine {
                            calls++
                            ok()
                        },
                        config,
                    ),
                )

            repo.get()
            val second = repo.get()

            assertThat(calls).isEqualTo(1)
            assertThat(second).isEqualTo(Result.Success(expected))
        }

    @Test
    fun `a falha nao fica em memoria e a proxima chamada tenta de novo`() =
        runTest {
            var calls = 0
            val repo = RemoteFiltersRepository(createHttpClient(MockEngine { if (calls++ == 0) down() else ok() }, config))

            repo.get()
            val second = repo.get()

            assertThat(calls).isEqualTo(2)
            assertThat(second).isEqualTo(Result.Success(expected))
        }
}
