// O tema do app, que todas as telas usam por fora.
// CompositionLocalProvider entrega um valor (a paleta dos selos) a todos os
// componentes de dentro, sem passar por parâmetro.

package com.velosobr.piuds.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Aplica as cores (dinâmicas, se pedidas e disponíveis, senão as da marca), a
 * tipografia, as formas e a paleta dos selos. Tudo o que fica dentro de
 * [content] lê esses valores pelo MaterialTheme e pelo LocalConservationPalette.
 */
@Composable
fun PiuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val conservation = if (darkTheme) DarkConservationPalette else LightConservationPalette
    val tokens = if (darkTheme) PiuDarkColors else PiuLightColors
    val dynamic = if (dynamicColor) dynamicColorScheme(darkTheme) else null
    CompositionLocalProvider(LocalConservationPalette provides conservation) {
        MaterialTheme(
            colorScheme = dynamic ?: tokens,
            typography = piuTypography(),
            shapes = PiuShapes,
            content = content,
        )
    }
}
