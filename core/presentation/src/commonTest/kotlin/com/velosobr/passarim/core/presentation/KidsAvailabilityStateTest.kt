// Testes da tradução da checagem do Kids (a resposta do repositório) no
// estado que a Escolha e as Configurações mostram.

package com.velosobr.passarim.core.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import kotlin.test.Test

class KidsAvailabilityStateTest {
    @Test
    fun `BFF com aves do Kids deixa o Kids disponivel`() {
        assertThat(Result.Success(true).toKidsAvailabilityState()).isEqualTo(KidsAvailabilityState.AVAILABLE)
    }

    @Test
    fun `BFF sem o Kids ou sem aves esconde o Kids`() {
        assertThat(Result.Success(false).toKidsAvailabilityState()).isEqualTo(KidsAvailabilityState.HIDDEN)
    }

    @Test
    fun `qualquer erro de rede vira sem rede`() {
        listOf(DataError.Network.NoConnection, DataError.Network.Timeout, DataError.Network.Internal, DataError.Network.Unknown)
            .forEach { error ->
                assertThat(Result.Failure(error).toKidsAvailabilityState()).isEqualTo(KidsAvailabilityState.OFFLINE)
            }
    }

    @Test
    fun `so o disponivel deixa entrar no Kids`() {
        assertThat(KidsAvailabilityState.entries.filter { it.canEnter }).isEqualTo(listOf(KidsAvailabilityState.AVAILABLE))
    }
}
