// Foto grande do topo do Detalhe e o botão redondo que fica sobre ela.

package com.velosobr.piuds.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velosobr.piuds.theme.PiuDimens

/** Proporção largura:altura da foto do topo. */
private const val HERO_ASPECT_RATIO = 1.3f

/**
 * A foto do topo, com o placeholder por baixo enquanto ela carrega (ou se não
 * houver). [overlay] é um "slot": um bloco @Composable que quem chama passa
 * para desenhar por cima (os botões de voltar e favoritar). AsyncImage é o
 * componente do Coil que baixa e mostra a imagem; ContentScale.Crop preenche o
 * espaço cortando as bordas.
 */
@Composable
fun PiuHeroPhoto(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier.fillMaxWidth().aspectRatio(HERO_ASPECT_RATIO)) {
        PhotoPlaceholder(iconSize = 96.dp)
        if (imageUrl != null) {
            AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        overlay()
    }
}

/** Botão redondo e translúcido para ficar legível sobre qualquer foto, com área de toque de 48dp. */
@Composable
fun PiuHeroIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    IconButton(onClick = onClick, modifier = modifier.size(PiuDimens.touchTarget)) {
        Box(
            Modifier.size(40.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = contentDescription, tint = tint)
        }
    }
}
