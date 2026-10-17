package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun PassarimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val conservation = if (darkTheme) DarkConservationPalette else LightConservationPalette
    val tokens = if (darkTheme) PassarimDarkColors else PassarimLightColors
    val dynamic = if (dynamicColor) dynamicColorScheme(darkTheme) else null
    CompositionLocalProvider(LocalConservationPalette provides conservation) {
        MaterialTheme(
            colorScheme = dynamic ?: tokens,
            typography = passarimTypography(),
            shapes = PassarimShapes,
            content = content,
        )
    }
}
