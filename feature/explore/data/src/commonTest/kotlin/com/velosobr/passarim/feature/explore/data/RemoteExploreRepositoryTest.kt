// Testes do repositório da lista com o MockEngine do Ktor no lugar do BFF.

package com.velosobr.passarim.feature.explore.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.data.createHttpClient
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.feature.explore.domain.SpeciesPage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/** Os parâmetros chegam certos na URL e a resposta vira a página do domínio. */
class RemoteExploreRepositoryTest {
    private val page =
        """
        {"items":[
          {"id":"bem-te-vi","commonName":"Bem-te-vi","scientificName":"Pitangus sulphuratus",
           "thumbnailUrl":"http://localhost:8888/buckets/passarim-media/bem-te-vi/thumb.jpg","conservationStatus":"LC"},
          {"id":"ararinha-azul","commonName":"Ararinha-azul","scientificName":"Cyanopsitta spixii",
           "thumbnailUrl":null,"conservationStatus":"EW"}
        ],"nextCursor":"c2"}
        """.trimIndent()

    /**
     * repo cria o repositório com um "servidor" que sempre responde [page];
     * [onRequest] recebe a URL de cada pedido e [mediaHostOverride] liga a troca de host.
     */
    private fun repo(
        mediaHostOverride: String? = null,
        onRequest: (Url) -> Unit = {},
    ): RemoteExploreRepository {
        val config =
            AppConfig(isDebug = false, baseUrl = "http://localhost:8080", mediaHostOverride = mediaHostOverride, versionName = "1.0.0")
        val engine =
            MockEngine { request ->
                onRequest(request.url)
                respond(page, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
            }
        return RemoteExploreRepository(createHttpClient(engine, config), MediaUrlRewriter(config))
    }

    @Test
    fun `manda busca filtros cursor e limite como query`() =
        runTest {
            var url: Url? = null
            repo { url = it }.list(query = "azul", biome = "cerrado", state = "SP", cursor = "c1")

            val params = url!!.parameters
            assertThat(url!!.encodedPath).isEqualTo("/v1/species")
            assertThat(params["q"]).isEqualTo("azul")
            assertThat(params["biome"]).isEqualTo("cerrado")
            assertThat(params["state"]).isEqualTo("SP")
            assertThat(params["cursor"]).isEqualTo("c1")
            assertThat(params["limit"]).isEqualTo("20")
        }

    @Test
    fun `busca vazia e filtros nulos nao vao na URL`() =
        runTest {
            var url: Url? = null
            repo { url = it }.list(query = "", biome = null, state = null, cursor = null)
            assertThat(url.toString()).isEqualTo("http://localhost:8080/v1/species?limit=20")
        }

    @Test
    fun `mapeia a pagina com thumbnail nulo e o proximo cursor`() =
        runTest {
            val result = repo().list(query = "", biome = null, state = null, cursor = null)
            assertThat(result).isEqualTo(
                Result.Success(
                    SpeciesPage(
                        items =
                            listOf(
                                SpeciesSummary(
                                    "bem-te-vi",
                                    "Bem-te-vi",
                                    "Pitangus sulphuratus",
                                    "http://localhost:8888/buckets/passarim-media/bem-te-vi/thumb.jpg",
                                ),
                                SpeciesSummary("ararinha-azul", "Ararinha-azul", "Cyanopsitta spixii", null),
                            ),
                        nextCursor = "c2",
                    ),
                ),
            )
        }

    @Test
    fun `o MediaUrlRewriter e aplicado ao thumbnail`() =
        runTest {
            val result = repo(mediaHostOverride = "10.0.2.2").list(query = "", biome = null, state = null, cursor = null)
            val items = (result as Result.Success).data.items
            assertThat(items[0].thumbnailUrl).isEqualTo("http://10.0.2.2:8888/buckets/passarim-media/bem-te-vi/thumb.jpg")
            assertThat(items[1].thumbnailUrl).isNull()
        }

    @Test
    fun `propaga o erro do safeCall`() =
        runTest {
            val config = AppConfig(isDebug = false, baseUrl = "http://localhost:8080", mediaHostOverride = null, versionName = "1.0.0")
            val engine =
                MockEngine { respond("404 page not found", HttpStatusCode.NotFound, headersOf(HttpHeaders.ContentType, "text/plain")) }
            val result = RemoteExploreRepository(createHttpClient(engine, config), MediaUrlRewriter(config)).list("", null, null, null)
            assertThat(result).isEqualTo(Result.Failure(DataError.Network.ServiceUnavailable))
        }
}
