// Declaração das cores dinâmicas de cada plataforma.

package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** Se a plataforma tem cores dinâmicas (a tela de Configurações só mostra a opção quando tem). */
expect val isDynamicColorSupported: Boolean

/** O esquema de cores do sistema, ou null quando não há. */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?
