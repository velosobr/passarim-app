// Testes da escolha do esquema de cores e de "reduzir animações": funções
// puras, sem Compose, então rodam em commonTest.

package com.velosobr.piuds

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.piuds.theme.PiuSchemeChoice
import com.velosobr.piuds.theme.resolvePiuScheme
import com.velosobr.piuds.theme.resolveReduceMotion
import kotlin.test.Test

class SchemeChoiceTest {
    @Test
    fun `o Kids e sempre claro e sem cores dinamicas`() {
        ThemeMode.entries.forEach { mode ->
            listOf(true, false).forEach { systemDark ->
                assertThat(resolvePiuScheme(Journey.KIDS, mode, systemDark, dynamicColor = true))
                    .isEqualTo(PiuSchemeChoice(Journey.KIDS, dark = false, dynamic = false))
            }
        }
    }

    @Test
    fun `o adulto segue o tema escolhido e as cores dinamicas`() {
        assertThat(resolvePiuScheme(Journey.ADULT, ThemeMode.DARK, systemDark = false, dynamicColor = true))
            .isEqualTo(PiuSchemeChoice(Journey.ADULT, dark = true, dynamic = true))
        assertThat(resolvePiuScheme(Journey.ADULT, ThemeMode.SYSTEM, systemDark = true, dynamicColor = false))
            .isEqualTo(PiuSchemeChoice(Journey.ADULT, dark = true, dynamic = false))
        assertThat(resolvePiuScheme(Journey.ADULT, ThemeMode.LIGHT, systemDark = true, dynamicColor = false).dark).isFalse()
    }

    @Test
    fun `o valor sobrescrito ganha do sistema e o sistema so e lido sem ele`() {
        var reads = 0
        val system = {
            reads++
            true
        }
        assertThat(resolveReduceMotion(override = false, system = system)).isFalse()
        assertThat(reads).isEqualTo(0)
        assertThat(resolveReduceMotion(override = null, system = system)).isTrue()
        assertThat(reads).isEqualTo(1)
    }
}
