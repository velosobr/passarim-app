package com.velosobr.passarim.core.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.presentation.resources.Res
import com.velosobr.passarim.core.presentation.resources.error_generic
import com.velosobr.passarim.core.presentation.resources.error_no_connection
import com.velosobr.passarim.core.presentation.resources.error_rate_limited
import com.velosobr.passarim.core.presentation.resources.error_service_unavailable
import kotlin.test.Test

class DataErrorUiTextTest {
    private fun DataError.Network.id() = (toUiText() as UiText.Resource).id

    @Test
    fun `sem rede e timeout viram Sem conexao`() {
        assertThat(DataError.Network.NoConnection.id()).isEqualTo(Res.string.error_no_connection)
        assertThat(DataError.Network.Timeout.id()).isEqualTo(Res.string.error_no_connection)
    }

    @Test
    fun `servidor fora e erro interno viram Servico indisponivel`() {
        assertThat(DataError.Network.ServiceUnavailable.id()).isEqualTo(Res.string.error_service_unavailable)
        assertThat(DataError.Network.Internal.id()).isEqualTo(Res.string.error_service_unavailable)
    }

    @Test
    fun `limite de requisicoes tem mensagem propria`() {
        assertThat(DataError.Network.RateLimited(30).id()).isEqualTo(Res.string.error_rate_limited)
    }

    @Test
    fun `o resto cai na mensagem generica`() {
        assertThat(DataError.Network.NotFound.id()).isEqualTo(Res.string.error_generic)
        assertThat(DataError.Network.InvalidParameter.id()).isEqualTo(Res.string.error_generic)
        assertThat(DataError.Network.Unknown.id()).isEqualTo(Res.string.error_generic)
        assertThat(DataError.Network.Unknown.toUiText()).isInstanceOf<UiText.Resource>()
    }
}
