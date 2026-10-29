// Título grande do topo das telas principais.
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja PiuBackTopBar.kt).

package com.velosobr.piuds.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.theme.PiuDimens

/** O título, com as margens da tela e marcado como cabeçalho para o leitor de tela. */
@Composable
fun PiuScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier =
            modifier
                .padding(start = PiuDimens.screenMargin, top = 24.dp, end = PiuDimens.screenMargin, bottom = 8.dp)
                .semantics { heading() },
    )
}
