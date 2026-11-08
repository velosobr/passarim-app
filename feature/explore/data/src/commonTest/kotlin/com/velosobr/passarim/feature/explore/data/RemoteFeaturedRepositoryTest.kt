// Testes do repositório da "Ave do dia" com o MockEngine do Ktor no lugar do
// BFF: o caminho pedido, a conversão da resposta e o 404 de quando não há ave.

package com.velosobr.passarim.feature.explore.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.data.createHttpClient
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.FeaturedSpecies
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.SpeciesSummary
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoteFeaturedRepositoryTest {
    private val config = AppConfig(isDebug = false, baseUrl = "http://localhost:8080", mediaHostOverride = null, versionName = "1.0.0")

    /** repo cria o repositório com um "servidor" que responde [body] com [status] e guarda o caminho pedido. */
    private fun repo(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        contentType: String = "application/json",
        onPath: (String) -> Unit = {},
    ): RemoteFeaturedRepository {
        val engine =
            MockEngine { request ->
                onPath(request.url.encodedPath)
                respond(body, status, headersOf(HttpHeaders.ContentType, contentType))
            }
        return RemoteFeaturedRepository(createHttpClient(engine, config), MediaUrlRewriter(config))
    }

    private val featured =
        """
        {"date":"2026-11-08",
         "species":{"id":"tuiuiu","commonName":"Tuiuiú","scientificName":"Jabiru mycteria",
           "thumbnailUrl":"http://localhost:8888/m/tuiuiu.jpg","conservationStatus":"LC","primaryBiome":"pantanal"},
         "audio":{"url":"http://localhost:8888/m/tuiuiu.mp3","durationMs":12000,
           "credit":{"author":"Fulano","license":"CC BY-NC","source":"xeno-canto","sourceUrl":"https://xeno-canto.org/1"}}}
        """.trimIndent()

    @Test
    fun `pede a ave do dia e converte a resposta`() =
        runTest {
            var path = ""
            val result = repo(featured) { path = it }.getFeaturedSpecies()
            assertThat(path).isEqualTo("/v1/featured")
            assertThat(result).isEqualTo(
                Result.Success(
                    FeaturedSpecies(
                        date = "2026-11-08",
                        species =
                            SpeciesSummary(
                                "tuiuiu",
                                "Tuiuiú",
                                "Jabiru mycteria",
                                "http://localhost:8888/m/tuiuiu.jpg",
                                Biome.PANTANAL,
                            ),
                        hasSong = true,
                    ),
                ),
            )
        }

    @Test
    fun `sem canto a ave do dia vem com hasSong falso`() =
        runTest {
            val semCanto =
                """
                {"date":"2026-11-08","audio":null,
                 "species":{"id":"seriema","commonName":"Seriema","scientificName":"Cariama cristata","thumbnailUrl":null}}
                """.trimIndent()
            val result = repo(semCanto).getFeaturedSpecies() as Result.Success<FeaturedSpecies>
            assertThat(result.data.hasSong).isEqualTo(false)
        }

    @Test
    fun `sem ave elegivel o 404 vira NotFound`() =
        runTest {
            val problem =
                """
                {"type":"urn:passarim:problem:featured-not-found","title":"Nenhuma ave do dia",
                 "status":404,"code":"FEATURED_NOT_FOUND"}
                """.trimIndent()
            // O BFF responde problem+json com o code FEATURED_NOT_FOUND.
            val result = repo(problem, HttpStatusCode.NotFound, contentType = "application/problem+json").getFeaturedSpecies()
            assertThat(result).isEqualTo(Result.Failure(DataError.Network.NotFound))
        }
}
