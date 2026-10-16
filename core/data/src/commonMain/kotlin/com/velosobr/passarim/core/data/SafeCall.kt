// Chamada segura ao BFF: transforma respostas e falhas em Result.
//
// Capturar `Exception` aqui é proposital: qualquer falha de transporte vira um erro tipado do app
// (o cancelamento de corrotina é sempre relançado antes).
@file:Suppress("TooGenericExceptionCaught", "SwallowedException")

package com.velosobr.passarim.core.data

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable

/**
 * Executa uma chamada e devolve sempre um [Result] tipado; nunca lança (exceto cancelamento).
 *
 * Erros de transporte viram [DataError.Network.Timeout] ou [DataError.Network.NoConnection].
 * Respostas de erro viram o erro do `code` do problem+json do BFF; se a resposta **não** for
 * problem+json (um 404 em texto puro do Traefik com as réplicas paradas, por exemplo),
 * classifica pelo status HTTP.
 *
 * "reified" (só possível em funções inline) mantém o tipo T disponível em
 * tempo de execução, e é isso que permite response.body<T>() saber em que
 * classe converter o JSON. CancellationException é relançada porque é o
 * sinal de que a coroutine foi cancelada (a tela fechou); engoli-la deixaria
 * a coroutine rodando à toa.
 */
suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response =
        try {
            execute()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Result.Failure(e.toNetworkError())
        }
    return if (response.status.isSuccess()) {
        try {
            Result.Success(response.body<T>())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(DataError.Network.Unknown)
        }
    } else {
        Result.Failure(response.toNetworkError())
    }
}

/** O corpo de erro problem+json do BFF; só o `code` interessa ao app. */
@Serializable
internal data class ProblemDto(
    val code: String? = null,
)

/**
 * Falha de transporte: estouro de prazo vira Timeout; o resto (sem rede, DNS,
 * conexão recusada) vira NoConnection. @PublishedApi deixa uma função
 * internal ser usada dentro de uma função inline pública.
 */
@PublishedApi
internal fun Exception.toNetworkError(): DataError.Network =
    when (this) {
        is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> DataError.Network.Timeout
        else -> DataError.Network.NoConnection
    }

/** Resposta de erro: lê o `code` do problem+json ou, sem ele, classifica pelo status. */
@PublishedApi
internal suspend fun HttpResponse.toNetworkError(): DataError.Network {
    val isProblem = contentType()?.match("application/problem+json") == true
    if (isProblem) {
        val code =
            try {
                bffJson.decodeFromString(ProblemDto.serializer(), bodyAsText()).code
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
        if (code != null) return code.toNetworkError(headers[HttpHeaders.RetryAfter]?.toIntOrNull())
    }
    return status.value.toNetworkError()
}

/** Traduz o `code` estável do contrato do BFF no erro do app. */
private fun String.toNetworkError(retryAfterSeconds: Int?): DataError.Network =
    when (this) {
        "SPECIES_NOT_FOUND", "NOT_FOUND" -> DataError.Network.NotFound
        "INVALID_PARAMETER", "METHOD_NOT_ALLOWED" -> DataError.Network.InvalidParameter
        "RATE_LIMITED" -> DataError.Network.RateLimited(retryAfterSeconds)
        "SERVICE_UNAVAILABLE" -> DataError.Network.ServiceUnavailable
        "INTERNAL" -> DataError.Network.Internal
        else -> DataError.Network.Unknown
    }

/**
 * Status sem problem+json: 404/502/503/504 costumam ser o Traefik sem réplica
 * do BFF no ar, então viram "serviço indisponível".
 */
private fun Int.toNetworkError(): DataError.Network =
    when (this) {
        404, 502, 503, 504 -> DataError.Network.ServiceUnavailable
        in 500..599 -> DataError.Network.Internal
        else -> DataError.Network.Unknown
    }
