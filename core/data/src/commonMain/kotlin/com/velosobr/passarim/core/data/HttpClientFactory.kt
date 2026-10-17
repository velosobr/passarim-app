// O cliente HTTP do BFF e a configuração do JSON.

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

/**
 * JSON do BFF: campos novos que o app ainda não conhece não quebram a leitura.
 * explicitNulls = false: um campo anulável sem valor padrão que falte no JSON
 * vira null (em vez de erro), e na escrita os campos null são omitidos.
 */
internal val bffJson =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

/**
 * Cliente HTTP do BFF. O [engine] é injetado (OkHttp no Android, Darwin no iOS, MockEngine nos testes).
 * O log do Ktor só existe em debug, para não vazar URLs em produção.
 *
 * Os plugins do Ktor acrescentam comportamentos ao cliente: ContentNegotiation
 * converte JSON em objetos, HttpTimeout põe prazos (5 s para conectar, 10 s
 * para a chamada inteira e 10 s sem receber dados), e defaultRequest completa
 * os caminhos relativos com a URL do BFF.
 * expectSuccess = false faz um status de erro NÃO virar exceção: quem trata é
 * o safeCall.
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
