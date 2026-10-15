package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

expect val isDynamicColorSupported: Boolean

@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?
