package com.velosobr.passarim.core.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.DataError
import kotlin.test.Test

class ScreenErrorTest {
    @Test
    fun `sem rede e timeout viram NoConnection`() {
        assertThat(DataError.Network.NoConnection.toScreenError()).isEqualTo(ScreenError.NoConnection)
        assertThat(DataError.Network.Timeout.toScreenError()).isEqualTo(ScreenError.NoConnection)
    }

    @Test
    fun `servidor fora e erro interno viram ServiceUnavailable`() {
        assertThat(DataError.Network.ServiceUnavailable.toScreenError()).isEqualTo(ScreenError.ServiceUnavailable)
        assertThat(DataError.Network.Internal.toScreenError()).isEqualTo(ScreenError.ServiceUnavailable)
    }

    @Test
    fun `limite de requisicoes carrega o Retry-After`() {
        assertThat(DataError.Network.RateLimited(30).toScreenError()).isEqualTo(ScreenError.RateLimited(30))
        assertThat(DataError.Network.RateLimited(null).toScreenError()).isEqualTo(ScreenError.RateLimited(null))
    }

    @Test
    fun `parametro invalido nao encontrado e desconhecido viram Generic`() {
        listOf(DataError.Network.InvalidParameter, DataError.Network.NotFound, DataError.Network.Unknown).forEach {
            assertThat(it.toScreenError()).isEqualTo(ScreenError.Generic)
        }
    }
}
