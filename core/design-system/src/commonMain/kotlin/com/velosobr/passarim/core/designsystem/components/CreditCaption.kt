// Legenda de crédito (autor · licença), em letra pequena.
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja BackTopBar.kt).

package com.velosobr.passarim.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Mostra o crédito de uma foto ou de um canto, como as licenças CC exigem. */
@Composable
fun CreditCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
}
