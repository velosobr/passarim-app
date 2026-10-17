// Os tipos de tela de erro, compartilhados por todas as features.
// "sealed": as únicas implementações são as declaradas aqui, então um "when"
// cobre todas as telas de erro sem "else".

package com.velosobr.passarim.core.presentation

import com.velosobr.passarim.core.domain.DataError

/**
 * Qual tela de erro mostrar quando o conteúdo principal não carregou. Cada feature escolhe o texto
 * (o que "não carregou" muda de tela para tela); a classificação é a mesma em todas.
 */
sealed interface ScreenError {
    /** "Sem conexão", com "Tentar novamente" (e um atalho para os favoritos, que funcionam offline). */
    data object NoConnection : ScreenError

    /** "Serviço indisponível": o BFF está fora ou falhou. */
    data object ServiceUnavailable : ScreenError

    /** Muitas requisições: "Tentar novamente" só volta a valer depois de [retryAfterSeconds]. */
    data class RateLimited(
        val retryAfterSeconds: Int?,
    ) : ScreenError

    /** Erros que a UI não deveria provocar (parâmetro inválido, por exemplo): mensagem genérica. */
    data object Generic : ScreenError
}

/** Escolhe a tela de erro para cada erro de rede. */
fun DataError.Network.toScreenError(): ScreenError =
    when (this) {
        DataError.Network.NoConnection, DataError.Network.Timeout -> ScreenError.NoConnection
        DataError.Network.ServiceUnavailable, DataError.Network.Internal -> ScreenError.ServiceUnavailable
        is DataError.Network.RateLimited -> ScreenError.RateLimited(retryAfterSeconds)
        DataError.Network.InvalidParameter, DataError.Network.NotFound, DataError.Network.Unknown -> ScreenError.Generic
    }
