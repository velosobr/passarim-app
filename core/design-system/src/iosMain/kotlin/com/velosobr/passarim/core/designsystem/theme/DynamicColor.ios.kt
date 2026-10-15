package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

actual val isDynamicColorSupported: Boolean = false

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
