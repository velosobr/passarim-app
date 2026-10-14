package com.velosobr.passarim.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import org.koin.core.qualifier.named

/** Fotos são maiores que as respostas da API e podem vir por redes lentas: timeouts mais folgados. */
const val MEDIA_REQUEST_TIMEOUT_MILLIS = 30_000L

/** Nome do `HttpClient` de mídia no Koin (o cliente sem nome é o da API). */
val MediaHttpClientQualifier = named("media")

/**
 * Cliente HTTP só para mídia (o Coil o usa para baixar as fotos). Não tem o `defaultRequest` do BFF,
 * porque as URLs de mídia já são absolutas e apontam para outro host, nem o JSON e o log da API.
 */
fun createMediaHttpClient(engine: HttpClientEngine): HttpClient =
    HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = MEDIA_REQUEST_TIMEOUT_MILLIS
            socketTimeoutMillis = MEDIA_REQUEST_TIMEOUT_MILLIS
        }
    }
