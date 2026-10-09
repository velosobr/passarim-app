package com.velosobr.passarim.core.data

import com.velosobr.passarim.core.domain.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** JSON do BFF: campos novos que o app ainda não conhece não quebram a leitura. */
internal val bffJson =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

/**
 * Cliente HTTP do BFF. O [engine] é injetado (OkHttp no Android, Darwin no iOS, MockEngine nos testes).
 * O log do Ktor só existe em debug, para não vazar URLs em produção.
 */
fun createHttpClient(
    engine: HttpClientEngine,
    config: AppConfig,
): HttpClient =
    HttpClient(engine) {
        install(ContentNegotiation) { json(bffJson) }
        install(HttpTimeout) {
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
        if (config.isDebug) {
            install(Logging) { level = LogLevel.INFO }
        }
        defaultRequest { url(config.baseUrl) }
        expectSuccess = false
    }
