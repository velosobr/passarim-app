// Título grande do topo das telas principais.

package com.velosobr.passarim.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens

/** O título, com as margens da tela e marcado como cabeçalho para o leitor de tela. */
@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier =
            modifier
                .padding(start = PassarimDimens.screenMargin, top = 24.dp, end = PassarimDimens.screenMargin, bottom = 8.dp)
                .semantics { heading() },
    )
}
