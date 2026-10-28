// Testes dos tokens: as cores do Piu e do Kids, os gradientes de bioma e de
// mundo, a tipografia (títulos em 800 no Piu, a tabela do Kids) e as sombras
// sólidas, conferidos contra os valores do passarim-tokens.json 2.0.0
// (copiados aqui), mais o contraste mínimo dos selos, do Kids e do texto
// sobre as duas pontas de cada gradiente.

package com.velosobr.piuds

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.piuds.theme.DarkConservationPalette
import com.velosobr.piuds.theme.LightConservationPalette
import com.velosobr.piuds.theme.PiuDarkColors
import com.velosobr.piuds.theme.PiuKidsColors
import com.velosobr.piuds.theme.PiuKidsTokens
import com.velosobr.piuds.theme.PiuLightColors
import com.velosobr.piuds.theme.TitleWeight
import com.velosobr.piuds.theme.kidsTypeSpecs
import com.velosobr.piuds.theme.piuAdultTokens
import com.velosobr.piuds.theme.piuTypeSpecs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test

/**
 * contrast calcula a razão de contraste da WCAG entre duas cores, a partir da
 * luminância (o brilho percebido): 4.5:1 é o mínimo para texto legível.
 */
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
        assertThat(PiuLightColors.primary).isEqualTo(Color(0xFF1A6D3C))
        assertThat(PiuLightColors.onPrimary).isEqualTo(Color(0xFFFFFFFF))
        assertThat(PiuDarkColors.primary).isEqualTo(Color(0xFF7DDB97))
        assertThat(PiuDarkColors.surface).isEqualTo(Color(0xFF101410))
    }

    @Test
    fun `fundo unico - background e igual a surface nos dois temas`() {
        assertThat(PiuLightColors.background).isEqualTo(PiuLightColors.surface)
        assertThat(PiuDarkColors.background).isEqualTo(PiuDarkColors.surface)
    }

    @Test
    fun `nenhum papel do esquema cai no baseline lilas do Material`() {
        // O baseline do Material 3 usa 0xFF6750A4 (roxo) como primary; qualquer papel herdado dele seria um bug.
        val baselinePurple = Color(0xFF6750A4)
        listOf(PiuLightColors, PiuDarkColors).forEach {
            assertThat(it.primary == baselinePurple).isEqualTo(false)
            assertThat(it.surfaceContainerHighest == Color(0xFFE6E0E9)).isEqualTo(false)
            assertThat(it.surfaceContainerLow == Color(0xFFF7F2FA)).isEqualTo(false)
        }
    }

    @Test
    fun `EX e DD batem com os tokens nos dois temas`() {
        assertThat(LightConservationPalette[ConservationStatus.EX].background).isEqualTo(Color(0xFFDDDDDD))
        assertThat(LightConservationPalette[ConservationStatus.DD].content).isEqualTo(Color(0xFF14304D))
        assertThat(DarkConservationPalette[ConservationStatus.EX].background).isEqualTo(Color(0xFF3C3C3C))
        assertThat(DarkConservationPalette[ConservationStatus.DD].content).isEqualTo(Color(0xFFD3E4F5))
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

    @Test
    fun `o esquema Kids bate com os tokens`() {
        assertThat(PiuKidsColors.primary).isEqualTo(Color(0xFFA84300))
        assertThat(PiuKidsColors.primaryContainer).isEqualTo(Color(0xFFFF8A3D))
        assertThat(PiuKidsColors.onPrimaryContainer).isEqualTo(Color(0xFF1D2B3A))
        assertThat(PiuKidsColors.surface).isEqualTo(Color(0xFFFFF7E8))
        assertThat(PiuKidsColors.background).isEqualTo(PiuKidsColors.surface)
        assertThat(PiuKidsColors.inversePrimary).isEqualTo(Color(0xFFFFC93C))
    }

    @Test
    fun `os textos do Kids passam no contraste sobre o creme e sobre o laranja`() {
        assertThat(contrast(PiuKidsColors.surface, PiuKidsColors.onSurface)).isGreaterThanOrEqualTo(4.5f)
        assertThat(contrast(PiuKidsColors.primaryContainer, PiuKidsColors.onPrimaryContainer)).isGreaterThanOrEqualTo(4.5f)
        assertThat(contrast(PiuKidsColors.primary, PiuKidsColors.onPrimary)).isGreaterThanOrEqualTo(4.5f)
    }

    @Test
    fun `os gradientes batem com os tokens e o texto passa nas duas pontas`() {
        val adult = piuAdultTokens(dark = false).gradients
        assertThat(adult.getValue(Biome.CERRADO).to).isEqualTo(Color(0xFF9A6E1E))
        assertThat(PiuKidsTokens.gradients.getValue(Biome.PANTANAL).to).isEqualTo(Color(0xFF438DDC))
        (adult.values + PiuKidsTokens.gradients.values).forEach {
            assertThat(contrast(it.from, it.content)).isGreaterThanOrEqualTo(4.5f)
            assertThat(contrast(it.to, it.content)).isGreaterThanOrEqualTo(4.5f)
        }
        assertThat(adult.keys).isEqualTo(Biome.entries.toSet())
        assertThat(PiuKidsTokens.gradients.keys).isEqualTo(Biome.entries.toSet())
    }

    @Test
    fun `a tipografia do Piu segue os tokens 2_0_0 - titulos em 800`() {
        assertThat(TitleWeight).isEqualTo(FontWeight(800))
        assertThat(piuTypeSpecs.size).isEqualTo(15)
        listOf("displayLarge", "displayMedium", "displaySmall", "headlineLarge", "headlineMedium", "headlineSmall", "titleLarge")
            .forEach { assertThat(piuTypeSpecs.getValue(it).weight).isEqualTo(800) }
        listOf("titleMedium", "titleSmall", "labelLarge", "labelMedium", "labelSmall")
            .forEach { assertThat(piuTypeSpecs.getValue(it).weight).isEqualTo(600) }
        listOf("bodyLarge", "bodyMedium", "bodySmall").forEach { assertThat(piuTypeSpecs.getValue(it).weight).isEqualTo(400) }
        assertThat(piuTypeSpecs.getValue("displayLarge").tracking).isEqualTo(-0.25f)
    }

    @Test
    fun `o Kids segue a tabela dos tokens`() {
        val body = kidsTypeSpecs.getValue("bodyLarge")
        assertThat(body.size).isEqualTo(18)
        assertThat(body.weight).isEqualTo(600)
        assertThat(kidsTypeSpecs.getValue("headlineLarge").fredoka).isEqualTo(true)
        assertThat(kidsTypeSpecs.getValue("labelLarge").weight).isEqualTo(800)
        assertThat(kidsTypeSpecs.size).isEqualTo(15)
    }

    @Test
    fun `so o Kids tem sombras solidas`() {
        assertThat(piuAdultTokens(dark = true).cardShadow).isEqualTo(null)
        assertThat(PiuKidsTokens.buttonShadow?.color).isEqualTo(Color(0xFFC9601E))
    }
}
