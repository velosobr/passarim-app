// Barras do sistema no iOS.

package com.velosobr.passarim.app.theme

import androidx.compose.runtime.Composable

/** No iOS, a cor da barra de status segue o modo do sistema, mesmo com o tema do app forçado (LIGHT ou DARK). */
@Composable
internal actual fun SystemBarsAppearance(darkTheme: Boolean) = Unit
