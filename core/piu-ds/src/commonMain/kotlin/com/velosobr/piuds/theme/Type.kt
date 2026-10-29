// Tipografia do tema Piu (adulto): a fonte Manrope e os 15 estilos do
// Material 3, da tabela "type.styles" dos tokens (versão 2.0.0).

package com.velosobr.piuds.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.manrope
import org.jetbrains.compose.resources.Font

/** Manrope variável: um só arquivo, com os pesos usados pelo app (400, 600, 700 e 800, o dos títulos). */
@Composable
fun manropeFontFamily(): FontFamily =
    FontFamily(
        listOf(400, 600, 700, 800).map { weight ->
            Font(
                resource = Res.font.manrope,
                weight = FontWeight(weight),
                variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
            )
        },
    )

/** O peso dos títulos do Piu (tokens 2.0.0, "type.titleWeight"): 800, o "extra-negrito" da Manrope. */
val TitleWeight = FontWeight(800)

/** A tabela "type.styles" de passarim-tokens.json 2.0.0: display, headline e titleLarge em 800. */
val piuTypeSpecs: Map<String, PiuTypeSpec> =
    mapOf(
        "displayLarge" to PiuTypeSpec(57, 64, 800, -0.25f),
        "displayMedium" to PiuTypeSpec(45, 52, 800, 0f),
        "displaySmall" to PiuTypeSpec(36, 44, 800, 0f),
        "headlineLarge" to PiuTypeSpec(32, 40, 800, 0f),
        "headlineMedium" to PiuTypeSpec(28, 36, 800, 0f),
        "headlineSmall" to PiuTypeSpec(24, 32, 800, 0f),
        "titleLarge" to PiuTypeSpec(22, 28, 800, 0f),
        "titleMedium" to PiuTypeSpec(16, 24, 600, 0.15f),
        "titleSmall" to PiuTypeSpec(14, 20, 600, 0.1f),
        "bodyLarge" to PiuTypeSpec(16, 24, 400, 0.5f),
        "bodyMedium" to PiuTypeSpec(14, 20, 400, 0.25f),
        "bodySmall" to PiuTypeSpec(12, 16, 400, 0.4f),
        "labelLarge" to PiuTypeSpec(14, 20, 600, 0.1f),
        "labelMedium" to PiuTypeSpec(12, 16, 600, 0.5f),
        "labelSmall" to PiuTypeSpec(11, 16, 600, 0.5f),
    )

/** Os 15 estilos do Material 3, todos em Manrope, montados a partir de [piuTypeSpecs]. */
@Composable
fun piuTypography(): Typography {
    val family = manropeFontFamily()

    fun style(name: String): TextStyle {
        val s = piuTypeSpecs.getValue(name)
        return TextStyle(
            fontFamily = family,
            fontWeight = FontWeight(s.weight),
            fontSize = s.size.sp,
            lineHeight = s.line.sp,
            letterSpacing = s.tracking.sp,
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
