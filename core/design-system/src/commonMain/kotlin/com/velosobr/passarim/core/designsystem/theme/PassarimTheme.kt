package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Tema do Passarim: cores dos tokens (claro/escuro), Manrope, formas e a paleta de conservação.
 *
 * [dynamicColor] já existe como parâmetro, mas fica sem efeito até a 5d ligar o seletor de
 * "Cores dinâmicas" (Android 12+). O selo de conservação nunca usa cores dinâmicas.
 */
@Composable
fun PassarimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val conservation = if (darkTheme) DarkConservationPalette else LightConservationPalette
    CompositionLocalProvider(LocalConservationPalette provides conservation) {
        MaterialTheme(
            colorScheme = if (darkTheme) PassarimDarkColors else PassarimLightColors,
            typography = passarimTypography(),
            shapes = PassarimShapes,
            content = content,
        )
    }
}
