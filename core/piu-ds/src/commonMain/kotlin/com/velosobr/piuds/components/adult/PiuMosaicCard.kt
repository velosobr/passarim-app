// Card da grade em mosaico do Explorar adulto (frente E): a foto ocupa o
// card inteiro, um scrim escurece a parte de baixo e o nome vai em branco
// por cima. Duas alturas, alta e baixa, que a grade alterna.

package com.velosobr.piuds.components.adult

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.favorite_add
import com.velosobr.piuds.resources.favorite_remove
import com.velosobr.piuds.resources.ic_favorite_filled
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.theme.PiuDimens
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.toBrush
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** As duas alturas do card no mosaico (prancheta Explorar: 220 e 168 dp). */
enum class PiuMosaicHeight(
    internal val minHeightDp: Int,
) {
    TALL(220),
    SHORT(168),
}

/**
 * Card do mosaico. Sem foto (ou enquanto ela carrega), o fundo é o
 * gradiente do [biome], para o card nunca ficar vazio. O nome e o nome
 * científico ficam sobre o scrim e quebram linha em vez de cortar. O
 * coração fica num círculo translúcido de 44 dp, com alvo de toque de 48 dp.
 */
@Composable
fun PiuMosaicCard(
    name: String,
    scientificName: String,
    imageUrl: String?,
    biome: Biome,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: PiuMosaicHeight = PiuMosaicHeight.TALL,
) {
    val tokens = PiuTheme.tokens
    Box(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = height.minHeightDp.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(tokens.gradients.getValue(biome).toBrush())
            .clickable(onClick = onClick),
    ) {
        if (imageUrl != null) {
            AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
        PhotoScrim(Modifier.matchParentSize(), startFraction = 0.4f)
        // O top de 56 dp guarda o lugar do coração: um nome longo empurra o card para baixo, nunca para cima dele.
        Column(Modifier.align(Alignment.BottomStart).padding(start = 14.dp, end = 14.dp, bottom = 12.dp, top = 56.dp)) {
            Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(scientificName, style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic, color = Color.White.copy(alpha = 0.9f))
        }
        FavoriteBubble(name, isFavorite, onToggleFavorite, Modifier.align(Alignment.TopEnd).padding(6.dp))
    }
}

/**
 * O scrim: degradê de transparente (até [startFraction] da altura) até o
 * scrim dos tokens, embaixo. Ele dá ao texto branco contraste sobre qualquer
 * foto (a ferramenta de contraste mede o pior caso, foto branca, na faixa
 * mais escura). colorStops dá a posição de cada cor, de 0 (topo) a 1 (base).
 */
@Composable
internal fun PhotoScrim(
    modifier: Modifier,
    startFraction: Float = PiuTheme.tokens.scrimStart,
) {
    val scrim = PiuTheme.tokens.scrim
    Box(modifier.background(Brush.verticalGradient(0f to scrim.copy(alpha = 0f), startFraction to scrim.copy(alpha = 0f), 1f to scrim)))
}

/** O coração do card: círculo translúcido de 44 dp dentro de um alvo de 48 dp. */
@Composable
private fun FavoriteBubble(
    name: String,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier,
) {
    val description = stringResource(if (isFavorite) Res.string.favorite_remove else Res.string.favorite_add, name)
    IconToggleButton(checked = isFavorite, onCheckedChange = { onToggle() }, modifier = modifier.size(PiuDimens.touchTarget)) {
        Box(Modifier.size(44.dp).background(Color.White.copy(alpha = 0.28f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(
                vectorResource(if (isFavorite) Res.drawable.ic_favorite_filled else Res.drawable.ic_favorites),
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
