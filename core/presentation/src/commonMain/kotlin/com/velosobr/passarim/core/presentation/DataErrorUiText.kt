// Mensagem curta de erro de rede, para avisos rápidos (como um Snackbar).

package com.velosobr.passarim.core.presentation

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.presentation.resources.Res
import com.velosobr.passarim.core.presentation.resources.error_generic
import com.velosobr.passarim.core.presentation.resources.error_no_connection
import com.velosobr.passarim.core.presentation.resources.error_rate_limited
import com.velosobr.passarim.core.presentation.resources.error_service_unavailable

/** Mensagem curta para cada erro de rede; as telas de erro completas, com ações, usam o [ScreenError]. */
fun DataError.Network.toUiText(): UiText =
    when (this) {
        DataError.Network.NoConnection, DataError.Network.Timeout -> UiText.Resource(Res.string.error_no_connection)
        DataError.Network.ServiceUnavailable, DataError.Network.Internal -> UiText.Resource(Res.string.error_service_unavailable)
        is DataError.Network.RateLimited -> UiText.Resource(Res.string.error_rate_limited)
        DataError.Network.NotFound, DataError.Network.InvalidParameter, DataError.Network.Unknown ->
            UiText.Resource(
                Res.string.error_generic,
            )
    }
