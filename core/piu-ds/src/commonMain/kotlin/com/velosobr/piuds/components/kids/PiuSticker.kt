// A figurinha do álbum do Kids: a foto da ave dentro da forma cookie, na cor
// do mundo, com borda branca. A que ainda falta é um espaço tracejado com
// "?", anunciado como "figurinha que falta".

package com.velosobr.piuds.components.kids

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.PiuCookieShape
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.toBrush

/**
 * Uma figurinha guardada: a foto em forma de cookie sobre o gradiente do
 * mundo, com borda branca de 4 dp, e o nome embaixo. Sem foto (ou enquanto
 * carrega), aparece só o gradiente.
 */
@Composable
fun PiuSticker(
    name: String,
    biome: Biome,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cookie = PiuCookieShape()
    Column(
        modifier.clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier
                .size(100.dp)
                // A borda branca da prancheta é o surfaceContainerLowest do esquema Kids.
                .border(4.dp, MaterialTheme.colorScheme.surfaceContainerLowest, cookie)
                .padding(4.dp)
                .clip(cookie)
                .background(
                    PiuTheme.tokens.gradients
                        .getValue(biome)
                        .toBrush(),
                ),
        ) {
            if (imageUrl != null) {
                AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
        Text(
            name,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/**
 * O espaço de uma figurinha que ainda falta: borda tracejada e "?".
 * [label] é o texto embaixo ("Ainda falta") e [description] o que o leitor
 * de tela diz ("figurinha que falta"). clearAndSetSemantics troca tudo o
 * que há dentro (o "?" e o rótulo) por essa única frase.
 */
@Composable
fun PiuMissingSticker(
    label: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier.clearAndSetSemantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier
                .padding(4.dp)
                .size(92.dp)
                .drawBehind {
                    // PathEffect.dashPathEffect desenha o contorno em traços de 10 px com 8 px de intervalo.
                    drawRoundRect(
                        color = colors.outline,
                        cornerRadius = CornerRadius(30.dp.toPx()),
                        style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("?", style = MaterialTheme.typography.displaySmall, color = colors.onSurfaceVariant)
        }
        Text(label, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, color = colors.onSurfaceVariant)
    }
}

/** Prévia: uma figurinha guardada (sem foto, só o gradiente) e uma que falta. */
@Preview
@Composable
private fun PiuStickerPreview() {
    PiuTheme(journey = Journey.KIDS) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PiuSticker("Tuiuiú", Biome.PANTANAL, imageUrl = null, onClick = {})
            PiuMissingSticker(label = "Ainda falta", description = "figurinha que falta")
        }
    }
}
