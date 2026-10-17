// Cores dinâmicas no Android (Material You).
// LocalContext é um CompositionLocal com o Context Android, necessário para
// ler as cores do sistema.

package com.velosobr.passarim.core.designsystem.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/** Cores dinâmicas existem a partir do Android 12 (API 31, "S"). */
actual val isDynamicColorSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Monta o esquema de cores a partir do papel de parede do aparelho. Em
 * versões antigas devolve null, e o tema usa as cores da marca.
 */
@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    val context = LocalContext.current
    return if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}
