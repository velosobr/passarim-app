// Testes da decisão do tema escuro.

package com.velosobr.passarim.app.theme

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.ThemeMode
import kotlin.test.Test

class ThemeModeTest {
    @Test
    fun `sistema segue o sistema`() {
        assertThat(ThemeMode.SYSTEM.isDark(systemInDark = true)).isTrue()
        assertThat(ThemeMode.SYSTEM.isDark(systemInDark = false)).isFalse()
    }

    @Test
    fun `claro e escuro ignoram o sistema`() {
        assertThat(ThemeMode.LIGHT.isDark(systemInDark = true)).isFalse()
        assertThat(ThemeMode.DARK.isDark(systemInDark = false)).isTrue()
    }
}
