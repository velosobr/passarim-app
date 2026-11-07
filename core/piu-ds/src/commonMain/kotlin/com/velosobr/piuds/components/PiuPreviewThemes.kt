// Moldura das prévias (@Preview) dos componentes do Piu: desenha o mesmo
// conteúdo no tema claro e no escuro, um embaixo do outro, como pede o
// README do PiuDS (seção 9). Só as prévias usam; fica interna ao módulo.

package com.velosobr.piuds.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.piuds.theme.PiuTheme

/** [content] no Piu claro e no Piu escuro, cada um sobre o fundo do próprio tema. */
@Composable
internal fun PiuLightAndDarkPreview(content: @Composable () -> Unit) {
    Column {
        listOf(ThemeMode.LIGHT, ThemeMode.DARK).forEach { mode ->
            PiuTheme(themeMode = mode) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Box(Modifier.padding(8.dp)) { content() }
                }
            }
        }
    }
}
