// Os tokens que o Material 3 não tem: gradientes de bioma (Piu) e de mundo
// (Kids), o scrim das fotos, as sombras sólidas do Kids e as cores de
// conservação. Chegam aos componentes por PiuTheme.tokens. Valores de
// docs/design/tokens/passarim-tokens.json (passarim-docs, versão 2.0.0).
// "@Immutable" promete ao Compose que o objeto nunca muda depois de criado:
// assim, um componente que recebe os tokens não é redesenhado à toa.

package com.velosobr.piuds.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome

/** Um degradê de [from] a [to], no ângulo dado, com a cor do texto que vai por cima. */
@Immutable
data class PiuGradient(
    val from: Color,
    val to: Color,
    val content: Color,
    val angleDegrees: Float,
)

/** Sombra "sólida" do Kids: uma faixa de cor deslocada para baixo, sem desfoque. */
@Immutable
data class PiuSolidShadow(
    val color: Color,
    val offset: Dp,
)

/**
 * Os tokens extras de uma variante. [gradients] é por bioma (no Kids, o
 * mundo); [scrim] é a cor escura que cobre a parte de baixo das fotos a
 * partir de [scrimStart] (fração da altura); as sombras só existem no Kids.
 */
@Immutable
data class PiuTokens(
    val gradients: Map<Biome, PiuGradient>,
    val scrim: Color,
    val scrimStart: Float,
    val cardShadow: PiuSolidShadow?,
    val buttonShadow: PiuSolidShadow?,
    val conservation: ConservationPalette,
)

/** O scrim dos tokens: #101410 com 80% de opacidade, a partir de 25% da altura da foto. */
private val Scrim = Color(0xFF101410).copy(alpha = 0.8f)
private const val SCRIM_START = 0.25f

private fun adultGradient(
    from: Long,
    to: Long,
) = PiuGradient(Color(from), Color(to), Color.White, angleDegrees = 135f)

private fun kidsGradient(
    from: Long,
    to: Long,
    text: Long,
) = PiuGradient(Color(from), Color(to), Color(text), angleDegrees = 150f)

/** Os gradientes de bioma do Piu ("gradient.biome"), com texto branco. */
private val AdultGradients =
    mapOf(
        Biome.AMAZONIA to adultGradient(0xFF063D27, 0xFF328556),
        Biome.MATA_ATLANTICA to adultGradient(0xFF0D4F45, 0xFF3C8361),
        Biome.CERRADO to adultGradient(0xFF6E3A0E, 0xFF9A6E1E),
        Biome.CAATINGA to adultGradient(0xFF5C2414, 0xFFB65D30),
        Biome.PANTANAL to adultGradient(0xFF0E3A57, 0xFF338095),
        Biome.PAMPA to adultGradient(0xFF3E4A12, 0xFF737B37),
    )

/** Os mundos do Kids ("kids.gradient.world"), com o texto escuro de cada um. */
private val KidsGradients =
    mapOf(
        Biome.AMAZONIA to kidsGradient(0xFF9BE15D, 0xFF1F9F56, 0xFF0B2A17),
        Biome.MATA_ATLANTICA to kidsGradient(0xFF6EE7C0, 0xFF13A38B, 0xFF0B2A17),
        Biome.CERRADO to kidsGradient(0xFFFFE066, 0xFFFF9F1C, 0xFF3A2200),
        Biome.CAATINGA to kidsGradient(0xFFFFB38A, 0xFFE65741, 0xFF3A1208),
        Biome.PANTANAL to kidsGradient(0xFF7DD3FC, 0xFF438DDC, 0xFF0A2440),
        Biome.PAMPA to kidsGradient(0xFFE4F57A, 0xFF7CB342, 0xFF1F2A0A),
    )

/** Os tokens do Piu: os gradientes são os mesmos nos dois temas; a conservação muda. */
fun piuAdultTokens(dark: Boolean): PiuTokens =
    PiuTokens(
        gradients = AdultGradients,
        scrim = Scrim,
        scrimStart = SCRIM_START,
        cardShadow = null,
        buttonShadow = null,
        conservation = if (dark) DarkConservationPalette else LightConservationPalette,
    )

/** Os tokens do Kids: mundos, sombras sólidas e a conservação do tema claro. */
val PiuKidsTokens =
    PiuTokens(
        gradients = KidsGradients,
        scrim = Scrim,
        scrimStart = SCRIM_START,
        cardShadow = PiuSolidShadow(Color(0xFFF2E3C6), 4.dp),
        buttonShadow = PiuSolidShadow(Color(0xFFC9601E), 5.dp),
        conservation = LightConservationPalette,
    )

/**
 * CompositionLocal: um valor que o tema "entrega" a toda a árvore de
 * composables de dentro, sem passar por parâmetro. "static" porque só muda
 * quando a jornada ou o tema mudam (aí a árvore toda é refeita mesmo).
 */
val LocalPiuTokens = staticCompositionLocalOf { piuAdultTokens(dark = false) }
