// Teste das chaves que ligam a foto de um card à foto do topo do Detalhe: uma
// para o card do mosaico e outra para o da "Ave do dia", porque a mesma ave
// pode estar nos dois ao mesmo tempo.

package com.velosobr.passarim.core.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SharedPhotoKeyTest {
    @Test
    fun `a chave da foto compartilhada e foto mais o id da ave`() {
        assertThat(speciesPhotoKey("jabiru-mycteria")).isEqualTo("foto-jabiru-mycteria")
    }

    @Test
    fun `a chave da foto da ave do dia e outra que a do mosaico da mesma ave`() {
        assertThat(featuredPhotoKey("jabiru-mycteria")).isEqualTo("foto-destaque-jabiru-mycteria")
    }
}
