// Card da grade em mosaico do Explorar adulto (frente E): a foto ocupa o
// card inteiro, um scrim escurece a parte de baixo e o nome vai em branco
// por cima. Duas alturas, alta e baixa, que a grade alterna.

package com.velosobr.piuds.components.adult

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.PiuLightAndDarkPreview
import com.velosobr.piuds.components.piuSharedPhoto
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

/**
 * Card do mosaico. Sem foto (ou enquanto ela carrega), o fundo é o
 * gradiente do [biome], para o card nunca ficar vazio. Com [sharedPhotoKey],
 * a foto viaja até a tela que tiver a mesma chave (o topo do Detalhe; veja
 * PiuSharedPhoto.kt). O nome e o nome
 * científico ficam sobre o scrim e quebram linha em vez de cortar. O
 * coração fica num círculo translúcido de 44 dp, com alvo de toque de 48 dp,
 * e é um controle à parte: tocar nele favorita, tocar no resto abre a ave.
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
    sharedPhotoKey: String? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = height.minHeightDp.dp)
            .clip(RoundedCornerShape(24.dp))
            // Role.Button: o leitor de tela diz "botão" depois do nome da ave.
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        // A camada da foto (o gradiente e a imagem por cima) é a que viaja até o Detalhe.
        Box(
            Modifier
                .matchParentSize()
                .piuSharedPhoto(sharedPhotoKey, RoundedCornerShape(24.dp))
                .background(
                    PiuTheme.tokens.gradients
                        .getValue(biome)
                        .toBrush(),
                ),
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            }
        }
        // O top de 56 dp guarda o lugar do coração: um nome longo empurra o card para baixo, nunca para
        // cima dele. O scrim fica atrás do bloco de texto e já está cheio quando o nome começa.
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .photoTextScrim(PiuTheme.tokens.scrim)
                .padding(start = 14.dp, end = 14.dp, bottom = 12.dp, top = 56.dp),
        ) {
            Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                scientificName,
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.9f),
            )
        }
        FavoriteBubble(name, isFavorite, onToggleFavorite, Modifier.align(Alignment.TopEnd).padding(6.dp))
    }
}

/**
 * O coração do card: círculo translúcido de 44 dp dentro de um alvo de 48 dp.
 * Ele fica no alto, fora do scrim: o círculo usa a cor do scrim, e não o
 * branco translúcido da prancheta, para o coração branco passar no contraste
 * (3:1) sobre uma foto clara.
 * IconToggleButton é um botão de liga/desliga: o leitor de tela anuncia se
 * está marcado, e a descrição diz o que o toque vai fazer.
 */
@Composable
private fun FavoriteBubble(
    name: String,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier,
) {
    val description = stringResource(if (isFavorite) Res.string.favorite_remove else Res.string.favorite_add, name)
    IconToggleButton(checked = isFavorite, onCheckedChange = { onToggle() }, modifier = modifier.size(PiuDimens.touchTarget)) {
        Box(Modifier.size(44.dp).background(PiuTheme.tokens.scrim, CircleShape), contentAlignment = Alignment.Center) {
            Icon(
                vectorResource(if (isFavorite) Res.drawable.ic_favorite_filled else Res.drawable.ic_favorites),
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/** Prévia: um card alto favorito e um baixo, sem foto, nos dois temas. */
@Preview
@Composable
private fun PiuMosaicCardPreview() {
    PiuLightAndDarkPreview {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PiuMosaicCard(
                name = "Arara-azul-grande",
                scientificName = "Anodorhynchus hyacinthinus",
                imageUrl = null,
                biome = Biome.PANTANAL,
                isFavorite = true,
                onToggleFavorite = {},
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            PiuMosaicCard(
                name = "Carcará",
                scientificName = "Caracara plancus",
                imageUrl = null,
                biome = Biome.CAATINGA,
                isFavorite = false,
                onToggleFavorite = {},
                onClick = {},
                modifier = Modifier.weight(1f),
                height = PiuMosaicHeight.SHORT,
            )
        }
    }
}
