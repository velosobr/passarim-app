package com.velosobr.passarim.core.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MediaHttpClientTest {
    @Test
    fun `nao herda a URL base do BFF`() =
        runTest {
            var seen = ""
            val client =
                createMediaHttpClient(
                    MockEngine { request ->
                        seen = request.url.toString()
                        respond("", HttpStatusCode.OK)
                    },
                )
            client.get("http://10.0.2.2:8888/buckets/passarim-media/a.jpg")
            assertThat(seen).isEqualTo("http://10.0.2.2:8888/buckets/passarim-media/a.jpg")
        }

    @Test
    fun `usa timeouts maiores que os da API`() =
        runTest {
            var timeout: HttpTimeoutConfig? = null
            val client =
                createMediaHttpClient(
                    MockEngine { request ->
                        timeout = request.getCapabilityOrNull(HttpTimeoutCapability)
                        respond("", HttpStatusCode.OK)
                    },
                )
            client.get("http://x/a.jpg")
            assertThat(timeout?.requestTimeoutMillis).isEqualTo(MEDIA_REQUEST_TIMEOUT_MILLIS)
        }
}
