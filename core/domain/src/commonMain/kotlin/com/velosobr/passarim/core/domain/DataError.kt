// Os erros que o app sabe tratar, organizados por origem.
// "sealed": as únicas implementações possíveis são as declaradas aqui, então
// um "when" cobre todos os casos sem "else".

package com.velosobr.passarim.core.domain

/**
 * Erros de dados do app. Hoje só há erros de rede (as chamadas ao BFF); o
 * banco local não tem erros próprios. "data object" é um objeto único com
 * toString legível, bom para erros que não carregam dados.
 */
sealed interface DataError : Error {
    /** Erros das chamadas ao BFF. */
    sealed interface Network : DataError {
        /** Sem rede, DNS ou conexão recusada. */
        data object NoConnection : Network

        /** Estourou o prazo do HttpTimeout. */
        data object Timeout : Network

        /** O BFF respondeu que o recurso não existe (SPECIES_NOT_FOUND ou NOT_FOUND). */
        data object NotFound : Network

        /** O BFF rejeitou um parâmetro ou o método (INVALID_PARAMETER ou METHOD_NOT_ALLOWED). */
        data object InvalidParameter : Network

        /** [retryAfterSeconds] vem do cabeçalho `Retry-After`; nulo se o BFF não mandou. */
        data class RateLimited(
            val retryAfterSeconds: Int?,
        ) : Network

        /** BFF fora do ar ou proxy sem réplica (SERVICE_UNAVAILABLE, ou 404/502/503/504 sem problem+json). */
        data object ServiceUnavailable : Network

        /** Erro interno do BFF (INTERNAL ou outro 5xx). */
        data object Internal : Network

        /** Resposta que o app não sabe classificar, inclusive corpo de sucesso ilegível. */
        data object Unknown : Network
    }
}
