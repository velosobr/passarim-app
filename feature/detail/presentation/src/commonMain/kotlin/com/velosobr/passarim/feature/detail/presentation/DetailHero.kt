// O topo do Detalhe (prancheta Detalhe.dc.html, frente E): a foto de ponta a
// ponta, que vai até atrás da barra de status, o nome da ave em branco sobre
// o scrim na base da foto, uma faixa que funde a foto ao fundo da tela e os
// botões de voltar e de favoritar em círculos de vidro fosco por cima.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.presentation.speciesPhotoKey
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail
import com.velosobr.passarim.feature.detail.presentation.resources.Res
import com.velosobr.passarim.feature.detail.presentation.resources.back
import com.velosobr.piuds.components.PiuBlurState
import com.velosobr.piuds.components.PiuBlurSurface
import com.velosobr.piuds.components.PiuHeroPhoto
import com.velosobr.piuds.components.adult.photoTextScrim
import com.velosobr.piuds.components.piuBlurSource
import com.velosobr.piuds.components.rememberPiuBlurState
import com.velosobr.piuds.resources.favorite_add
import com.velosobr.piuds.resources.favorite_remove
import com.velosobr.piuds.resources.ic_arrow_back
import com.velosobr.piuds.resources.ic_favorite_filled
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.theme.PiuDimens
import com.velosobr.piuds.theme.PiuTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.piuds.resources.Res as DesignRes

/** A altura da foto de ponta a ponta do topo (prancheta Detalhe.dc.html). */
val HERO_HEIGHT = 430.dp

/** O gradiente atrás da foto de uma ave sem bioma principal (BFF antigo): o do Pantanal, como no mosaico. */
private val FallbackBiome = Biome.PANTANAL

/** A altura da faixa que funde a base da foto (já escurecida pelo scrim) à cor do fundo da tela. */
private val FadeHeight = 32.dp

/**
 * O topo: foto, nome e botões. A caixa tem no mínimo [HERO_HEIGHT]; com fonte grande, o nome
 * empurra a caixa para baixo em vez de ser cortado. A foto é a fonte do desfoque
 * (piuBlurSource): os botões desfocam o que está atrás deles. Ao abrir pelo mosaico, ela chega
 * voando do card (a mesma chave speciesPhotoKey). Ela fica numa camada à parte dos
 * botões porque uma superfície não pode desfocar a si mesma.
 *
 * O nome fica num bloco com o photoTextScrim, o degradê escuro do PiuDS que garante o contraste
 * do branco sobre qualquer foto (inclusive uma foto toda branca). Embaixo dele, uma faixa vai da
 * cor do scrim à cor do fundo do tema, e a foto se funde à página: no tema escuro a emenda some,
 * no claro vira uma transição curta do escuro para o claro.
 */
@Composable
internal fun DetailHero(
    detail: SpeciesDetail,
    isFavorite: Boolean,
    onAction: (DetailAction) -> Unit,
) {
    val blur = rememberPiuBlurState()
    val scrim = PiuTheme.tokens.scrim
    Box(Modifier.fillMaxWidth().heightIn(min = HERO_HEIGHT)) {
        PiuHeroPhoto(
            imageUrl = detail.photos.firstOrNull()?.heroUrl,
            biome = detail.primaryBiome ?: FallbackBiome,
            aspectRatio = null,
            sharedPhotoKey = speciesPhotoKey(detail.id),
            modifier = Modifier.matchParentSize().piuBlurSource(blur),
        )
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth()) {
            // Guarda o lugar da barra de status e dos botões: um nome longo nunca sobe para baixo deles.
            Spacer(Modifier.statusBarsPadding().height(PiuDimens.touchTarget + 32.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .photoTextScrim(scrim, fade = 48.dp)
                    .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    detail.commonName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                Text(
                    detail.scientificName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(FadeHeight)
                    .background(Brush.verticalGradient(listOf(scrim, MaterialTheme.colorScheme.background))),
            )
        }
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BackButton(blur, onBack = { onAction(DetailAction.Back) })
            FavoriteButton(blur, detail.commonName, isFavorite, onToggle = { onAction(DetailAction.ToggleFavorite) })
        }
    }
}

/**
 * O botão de voltar: um círculo de vidro fosco de 48 dp (PiuBlurSurface) com a tinta translúcida
 * da superfície do tema, que dá contraste ao ícone mesmo onde o desfoque não roda.
 */
@Composable
internal fun BackButton(
    blur: PiuBlurState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCircle(blur, modifier) {
        IconButton(onClick = onBack, modifier = Modifier.size(PiuDimens.touchTarget)) {
            Icon(vectorResource(DesignRes.drawable.ic_arrow_back), contentDescription = stringResource(Res.string.back))
        }
    }
}

/**
 * O coração: IconToggleButton é um botão de liga/desliga, então o leitor de tela anuncia se a ave
 * já está nos favoritos, e a descrição diz o que o toque vai fazer ("Favoritar Tuiuiú").
 */
@Composable
private fun FavoriteButton(
    blur: PiuBlurState,
    name: String,
    isFavorite: Boolean,
    onToggle: () -> Unit,
) {
    GlassCircle(blur) {
        IconToggleButton(checked = isFavorite, onCheckedChange = { onToggle() }, modifier = Modifier.size(PiuDimens.touchTarget)) {
            Icon(
                vectorResource(if (isFavorite) DesignRes.drawable.ic_favorite_filled else DesignRes.drawable.ic_favorites),
                contentDescription =
                    stringResource(
                        if (isFavorite) DesignRes.string.favorite_remove else DesignRes.string.favorite_add,
                        name,
                    ),
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** O círculo de vidro fosco de 48 dp que os dois botões usam. */
@Composable
private fun GlassCircle(
    blur: PiuBlurState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    PiuBlurSurface(
        state = blur,
        shape = CircleShape,
        tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        modifier = modifier.size(PiuDimens.touchTarget),
    ) { content() }
}
