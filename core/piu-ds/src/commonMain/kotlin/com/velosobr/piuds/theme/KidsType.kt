// Tipografia do Piu Kids: Fredoka (títulos, arredondada) e Nunito (texto),
// as duas com licença OFL e embutidas como fontes variáveis. Os 15 estilos
// do Material vêm da tabela do bloco "kids.type" dos tokens.

package com.velosobr.piuds.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.fredoka
import com.velosobr.piuds.resources.nunito
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

/** A tabela "kids.type.styles" de passarim-tokens.json 2.0.0, estilo por estilo. */
val kidsTypeSpecs: Map<String, KidsTypeSpec> =
    mapOf(
        "displayLarge" to KidsTypeSpec(fredoka = true, size = 57, line = 64, weight = 700),
        "displayMedium" to KidsTypeSpec(fredoka = true, size = 45, line = 52, weight = 700),
        "displaySmall" to KidsTypeSpec(fredoka = true, size = 38, line = 44, weight = 700),
        "headlineLarge" to KidsTypeSpec(fredoka = true, size = 34, line = 40, weight = 700),
        "headlineMedium" to KidsTypeSpec(fredoka = true, size = 28, line = 34, weight = 700),
        "headlineSmall" to KidsTypeSpec(fredoka = true, size = 26, line = 32, weight = 700),
        "titleLarge" to KidsTypeSpec(fredoka = true, size = 22, line = 28, weight = 700),
        "titleMedium" to KidsTypeSpec(fredoka = true, size = 18, line = 24, weight = 700),
        "titleSmall" to KidsTypeSpec(fredoka = true, size = 16, line = 22, weight = 600),
        "bodyLarge" to KidsTypeSpec(fredoka = false, size = 18, line = 26, weight = 600),
        "bodyMedium" to KidsTypeSpec(fredoka = false, size = 16, line = 24, weight = 600),
        "bodySmall" to KidsTypeSpec(fredoka = false, size = 14, line = 20, weight = 600),
        "labelLarge" to KidsTypeSpec(fredoka = false, size = 16, line = 22, weight = 800),
        "labelMedium" to KidsTypeSpec(fredoka = false, size = 14, line = 20, weight = 800),
        "labelSmall" to KidsTypeSpec(fredoka = false, size = 12, line = 16, weight = 700),
    )

/**
 * Uma família a partir de um arquivo variável: um só .ttf, com um Font por
 * peso usado. "FontVariation.weight" escolhe o ponto do eixo de peso da
 * fonte variável (um arquivo que contém todos os pesos).
 */
@Composable
private fun variableFamily(
    resource: FontResource,
    weights: List<Int>,
): FontFamily =
    FontFamily(
        weights.map { weight ->
            Font(resource = resource, weight = FontWeight(weight), variationSettings = FontVariation.Settings(FontVariation.weight(weight)))
        },
    )

/** Os 15 estilos do Material 3 com as fontes e os tamanhos do Kids. */
@Composable
fun piuKidsTypography(): Typography {
    val fredoka = variableFamily(Res.font.fredoka, listOf(600, 700))
    val nunito = variableFamily(Res.font.nunito, listOf(600, 700, 800))

    fun style(name: String): TextStyle {
        val s = kidsTypeSpecs.getValue(name)
        return TextStyle(
            fontFamily = if (s.fredoka) fredoka else nunito,
            fontWeight = FontWeight(s.weight),
            fontSize = s.size.sp,
            lineHeight = s.line.sp,
        )
    }
    return Typography(
        displayLarge = style("displayLarge"),
        displayMedium = style("displayMedium"),
        displaySmall = style("displaySmall"),
        headlineLarge = style("headlineLarge"),
        headlineMedium = style("headlineMedium"),
        headlineSmall = style("headlineSmall"),
        titleLarge = style("titleLarge"),
        titleMedium = style("titleMedium"),
        titleSmall = style("titleSmall"),
        bodyLarge = style("bodyLarge"),
        bodyMedium = style("bodyMedium"),
        bodySmall = style("bodySmall"),
        labelLarge = style("labelLarge"),
        labelMedium = style("labelMedium"),
        labelSmall = style("labelSmall"),
    )
}
