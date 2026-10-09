package com.velosobr.passarim.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import com.velosobr.passarim.core.designsystem.theme.DarkConservationPalette
import com.velosobr.passarim.core.designsystem.theme.LightConservationPalette
import com.velosobr.passarim.core.designsystem.theme.PassarimDarkColors
import com.velosobr.passarim.core.designsystem.theme.PassarimLightColors
import com.velosobr.passarim.core.domain.ConservationStatus
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test

class TokensTest {
    private fun contrast(
        a: Color,
        b: Color,
    ): Float {
        val hi = max(a.luminance(), b.luminance())
        val lo = min(a.luminance(), b.luminance())
        return (hi + 0.05f) / (lo + 0.05f)
    }

    @Test
    fun `cores de marca batem com os tokens nos dois temas`() {
        assertThat(PassarimLightColors.primary).isEqualTo(Color(0xFF1A6D3C))
        assertThat(PassarimLightColors.onPrimary).isEqualTo(Color(0xFFFFFFFF))
        assertThat(PassarimDarkColors.primary).isEqualTo(Color(0xFF7DDB97))
        assertThat(PassarimDarkColors.surface).isEqualTo(Color(0xFF101410))
    }

    @Test
    fun `fundo unico - background e igual a surface nos dois temas`() {
        assertThat(PassarimLightColors.background).isEqualTo(PassarimLightColors.surface)
        assertThat(PassarimDarkColors.background).isEqualTo(PassarimDarkColors.surface)
    }

    @Test
    fun `nenhum papel do esquema cai no baseline lilas do Material`() {
        // O baseline do Material 3 usa 0xFF6750A4 (roxo) como primary; qualquer papel herdado dele seria um bug.
        val baselinePurple = Color(0xFF6750A4)
        listOf(PassarimLightColors, PassarimDarkColors).forEach {
            assertThat(it.primary == baselinePurple).isEqualTo(false)
            assertThat(it.surfaceContainerHighest == Color(0xFFE6E0E9)).isEqualTo(false)
            assertThat(it.surfaceContainerLow == Color(0xFFF7F2FA)).isEqualTo(false)
        }
    }

    @Test
    fun `as paletas de conservacao tem todos os status com contraste minimo de 4_5`() {
        listOf(LightConservationPalette, DarkConservationPalette).forEach { palette ->
            ConservationStatus.entries.forEach { status ->
                val c = palette[status]
                assertThat(contrast(c.background, c.content)).isGreaterThanOrEqualTo(4.5f)
            }
        }
    }
}
