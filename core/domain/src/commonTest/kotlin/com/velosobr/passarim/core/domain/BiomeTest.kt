// Testes do bioma: o código do BFF vira o enum, e código desconhecido vira null.

package com.velosobr.passarim.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class BiomeTest {
    @Test
    fun `cada codigo do BFF vira o seu bioma`() {
        Biome.entries.forEach { assertThat(biomeOf(it.code)).isEqualTo(it) }
        assertThat(biomeOf("mata_atlantica")).isEqualTo(Biome.MATA_ATLANTICA)
    }

    @Test
    fun `codigo desconhecido ou nulo vira null`() {
        assertThat(biomeOf("lua")).isNull()
        assertThat(biomeOf(null)).isNull()
    }
}
