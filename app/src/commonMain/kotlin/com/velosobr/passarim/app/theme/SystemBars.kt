// Declaração da cor dos ícones das barras do sistema por plataforma.

package com.velosobr.passarim.app.theme

import androidx.compose.runtime.Composable

/** Ajusta os ícones das barras do sistema ao tema (no iOS, não faz nada). */
@Composable
internal expect fun SystemBarsAppearance(darkTheme: Boolean)
