// O cartão de um "mundo" (bioma) do Kids: o gradiente do mundo, o nome e,
// no álbum, o progresso ("4 de 9"). Também afunda ao tocar.

package com.velosobr.piuds.components.kids

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.toBrush

/**
 * O cartão do mundo [biome]. [name] e [progress] chegam prontos (o progresso
 * vem de fora, das contagens do catalog: nada aqui fixa quantas aves há). O
 * texto usa a cor de texto do próprio mundo, que passa no contraste nas duas
 * pontas do gradiente. A sombra é a ponta escura do gradiente, 25% mais
 * escura (lerp mistura duas cores na proporção dada).
 */
@Composable
fun PiuWorldCard(
    biome: Biome,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progress: String? = null,
) {
    val gradient = PiuTheme.tokens.gradients.getValue(biome)
    PiuSinkingSurface(
        face = gradient.toBrush(),
        shadowColor = lerp(gradient.to, Color.Black, 0.25f),
        shadowOffset = 5.dp,
        shape = RoundedCornerShape(26.dp),
        minHeight = 76.dp,
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium, color = gradient.content, textAlign = TextAlign.Center)
            if (progress != null) {
                Text(progress, style = MaterialTheme.typography.labelLarge, color = gradient.content, textAlign = TextAlign.Center)
            }
        }
    }
}

/** Prévia: dois mundos lado a lado, um com o progresso do álbum. */
@Preview
@Composable
private fun PiuWorldCardPreview() {
    PiuTheme(journey = Journey.KIDS) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PiuWorldCard(Biome.AMAZONIA, "Amazônia", onClick = {}, modifier = Modifier.weight(1f))
            PiuWorldCard(Biome.CAATINGA, "Caatinga", onClick = {}, progress = "4 de 9", modifier = Modifier.weight(1f))
        }
    }
}
