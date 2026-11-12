// Teste da chave que liga a foto do card à foto do topo do Detalhe.

package com.velosobr.passarim.core.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SharedPhotoKeyTest {
    @Test
    fun `a chave da foto compartilhada e foto mais o id da ave`() {
        assertThat(speciesPhotoKey("jabiru-mycteria")).isEqualTo("foto-jabiru-mycteria")
    }
}
