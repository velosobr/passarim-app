// Testes do Result e das funções de apoio. assertk é a biblioteca de
// asserções: assertThat(valor).isEqualTo(esperado).

package com.velosobr.passarim.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ResultTest {
    private val ok: Result<Int, DataError.Network> = Result.Success(2)
    private val fail: Result<Int, DataError.Network> = Result.Failure(DataError.Network.Timeout)

    @Test
    fun `map transforma o sucesso`() {
        assertThat(ok.map { it * 10 }).isEqualTo(Result.Success(20))
    }

    @Test
    fun `map preserva a falha`() {
        assertThat(fail.map { it * 10 }).isEqualTo(Result.Failure(DataError.Network.Timeout))
    }

    @Test
    fun `asEmptyDataResult descarta o dado e mantem a falha`() {
        assertThat(ok.asEmptyDataResult()).isEqualTo(Result.Success(Unit))
        assertThat(fail.asEmptyDataResult()).isEqualTo(Result.Failure(DataError.Network.Timeout))
    }

    @Test
    fun `onSuccess e onFailure executam so o ramo correspondente`() {
        val seen = mutableListOf<String>()
        ok.onSuccess { seen += "ok:$it" }.onFailure { seen += "erro" }
        fail.onSuccess { seen += "ok:$it" }.onFailure { seen += "erro:${it::class.simpleName}" }
        assertThat(seen).isEqualTo(listOf("ok:2", "erro:Timeout"))
    }

    @Test
    fun `RateLimited carrega o Retry-After`() {
        assertThat(DataError.Network.RateLimited(30).retryAfterSeconds).isEqualTo(30)
    }
}
