// Testes de UiText.

package com.velosobr.passarim.core.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class UiTextTest {
    @Test
    fun `DynamicString guarda o texto sem transformar`() {
        assertThat(UiText.DynamicString("Bem-te-vi").value).isEqualTo("Bem-te-vi")
    }

    @Test
    fun `DynamicString com mesmo texto e igual`() {
        assertThat(UiText.DynamicString("a")).isEqualTo(UiText.DynamicString("a"))
    }
}
