// Tela de estado vazio ou de erro, compartilhada por todas as features.
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja PiuBackTopBar.kt).

package com.velosobr.piuds.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Tela de estado (vazio ou erro): ícone num círculo de 120dp, título, texto e as ações. As ações são
 * um slot: cada tela escolhe os botões (por exemplo "Tentar novamente" e "Ver favoritos").
 */
@Composable
fun PiuStateScreen(
    icon: Painter,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    tone: StateTone = StateTone.Neutral,
    actions: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val (artBackground, artContent) =
        when (tone) {
            StateTone.Neutral -> colors.primaryContainer to colors.onPrimaryContainer
            StateTone.Error -> colors.surfaceVariant to colors.error
        }
    Column(
        modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Box(Modifier.size(120.dp).background(artBackground, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = artContent, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = colors.onSurface, textAlign = TextAlign.Center)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        actions()
    }
}
