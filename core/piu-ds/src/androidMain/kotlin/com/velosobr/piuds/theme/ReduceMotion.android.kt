// "Reduzir animações" no Android: a opção "Remover animações" das
// configurações de acessibilidade grava 0 na escala de duração das animações.
// Settings.Global são as configurações globais do aparelho, lidas pelo
// ContentResolver (o "porteiro" que o Android usa para dar acesso a dados de
// outros apps e do sistema).

package com.velosobr.piuds.theme

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun systemReduceMotionReader(): () -> Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) { { Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f } }
}
