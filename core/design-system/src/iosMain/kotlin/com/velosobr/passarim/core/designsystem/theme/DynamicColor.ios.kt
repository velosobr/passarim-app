// Cores dinâmicas no iOS: não existem.

package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** O iOS não tem cores dinâmicas do sistema. */
actual val isDynamicColorSupported: Boolean = false

/** Sem cores dinâmicas, o tema usa as cores da marca. */
@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
