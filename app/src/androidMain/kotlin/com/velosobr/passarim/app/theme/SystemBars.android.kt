// Cor dos ícones das barras do sistema no Android.
// LocalView é um CompositionLocal com a View Android onde o Compose desenha;
// dela se chega à janela (Window) da Activity.

package com.velosobr.passarim.app.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * O app desenha atrás das barras do sistema (relógio, bateria, navegação), e
 * os ícones delas precisam contrastar com o fundo: escuros no tema claro,
 * claros no tema escuro. SideEffect roda depois de cada composição bem-sucedida,
 * mantendo a janela em sincronia com o tema.
 */
@Composable
internal actual fun SystemBarsAppearance(darkTheme: Boolean) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
