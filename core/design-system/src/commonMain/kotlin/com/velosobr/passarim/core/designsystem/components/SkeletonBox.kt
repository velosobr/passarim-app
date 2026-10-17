// Bloco de carregamento (skeleton), usado para desenhar o "esqueleto" da tela
// enquanto os dados chegam.
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja BackTopBar.kt).

package com.velosobr.passarim.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Bloco cinza do carregamento (skeleton). O tamanho vem do [modifier]. */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f), shape))
}
