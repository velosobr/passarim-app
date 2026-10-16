// Os erros que o app sabe tratar, organizados por origem.

package com.velosobr.passarim.core.domain

/**
 * Erros de dados do app. Hoje só há erros de rede (as chamadas ao BFF); o
 * banco local não tem erros próprios. "data object" é um objeto único com
 * toString legível, bom para erros que não carregam dados.
 */
sealed interface DataError : Error {
    sealed interface Network : DataError {
        data object NoConnection : Network

        data object Timeout : Network

        data object NotFound : Network

        data object InvalidParameter : Network

        /** [retryAfterSeconds] vem do cabeçalho `Retry-After`; nulo se o BFF não mandou. */
        data class RateLimited(
            val retryAfterSeconds: Int?,
        ) : Network

        data object ServiceUnavailable : Network

        data object Internal : Network

        data object Unknown : Network
    }
}
