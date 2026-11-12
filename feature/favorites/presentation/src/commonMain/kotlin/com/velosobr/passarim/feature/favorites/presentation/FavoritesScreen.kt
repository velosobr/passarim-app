// A tela da aba Favoritos (frente E3): o mesmo mosaico do Explorar, com o
// título e a contagem de aves guardadas rolando junto com os cards, ou o
// estado vazio. Os cards (PiuMosaicCard) e as regras de colunas e alturas
// vêm do PiuDS; o FavoritesRoot liga esta tela ao ViewModel.

package com.velosobr.passarim.feature.favorites.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.feature.favorites.presentation.resources.Res
import com.velosobr.passarim.feature.favorites.presentation.resources.empty_body
import com.velosobr.passarim.feature.favorites.presentation.resources.empty_title
import com.velosobr.passarim.feature.favorites.presentation.resources.explore_birds
import com.velosobr.passarim.feature.favorites.presentation.resources.favorites_count
import com.velosobr.passarim.feature.favorites.presentation.resources.favorites_title
import com.velosobr.piuds.components.PiuStateScreen
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.components.adult.piuMosaicColumns
import com.velosobr.piuds.components.adult.piuMosaicHeightFor
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.theme.PiuDimens
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.velosobr.piuds.resources.Res as DesignRes

/** O gradiente de um card sem bioma principal: o do Pantanal, como no Explorar. */
private val FallbackBiome = Biome.PANTANAL

/**
 * A tela sem estado próprio: tudo vem de [state] e cada toque vira uma [FavoritesAction]. Enquanto o
 * banco não responde, mostra só o título. [contentPadding] é o espaço da barra flutuante embaixo: a
 * grade rola por baixo dela (o desfoque precisa do conteúdo atrás) e termina acima dela.
 */
@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onAction: (FavoritesAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    when (state) {
        FavoritesState.Loading -> FavoritesHeader(count = null, modifier.padding(horizontal = PiuDimens.screenMargin))
        FavoritesState.Empty -> EmptyFavorites(onAction, contentPadding, modifier)
        is FavoritesState.Content -> FavoritesMosaic(state, onAction, contentPadding, gridState, modifier)
    }
}

/**
 * O título "Favoritos" (marcado como cabeçalho para o leitor de tela) e, com aves, a contagem
 * ("4 aves guardadas"). pluralStringResource escolhe a frase no singular ou no plural pela regra
 * do idioma do aparelho. As margens laterais ficam com quem chama: na grade, elas já vêm do
 * contentPadding dela.
 */
@Composable
private fun FavoritesHeader(
    count: Int?,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(top = 24.dp, bottom = 2.dp)) {
        Text(
            stringResource(Res.string.favorites_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        if (count != null) {
            Text(
                pluralStringResource(Res.plurals.favorites_count, count, count),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * O mosaico: o cabeçalho ocupando a largura toda (FullLine) como primeiro item da grade, para rolar
 * junto com os cards, e as aves em colunas desencontradas. LazyVerticalStaggeredGrid é a grade em que
 * cada item tem a própria altura e entra na coluna mais curta; ela só cria os cards visíveis. Com
 * fonte grande, a grade vira uma coluna (piuMosaicColumns). animateItem anima a saída do card
 * desfavoritado e o deslizar dos outros.
 */
@Composable
private fun FavoritesMosaic(
    state: FavoritesState.Content,
    onAction: (FavoritesAction) -> Unit,
    contentPadding: PaddingValues,
    gridState: LazyStaggeredGridState,
    modifier: Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(piuMosaicColumns(LocalDensity.current.fontScale)),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = PiuDimens.screenMargin,
                end = PiuDimens.screenMargin,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
    ) {
        item(key = "header", span = StaggeredGridItemSpan.FullLine, contentType = "header") {
            FavoritesHeader(count = state.count)
        }
        itemsIndexed(state.items, key = { _, bird -> bird.id }, contentType = { _, _ -> "species" }) { index, bird ->
            PiuMosaicCard(
                name = bird.commonName,
                scientificName = bird.scientificName,
                imageUrl = bird.thumbnailUrl,
                biome = bird.primaryBiome ?: FallbackBiome,
                isFavorite = true,
                onToggleFavorite = { onAction(FavoritesAction.ToggleFavorite(bird)) },
                onClick = { onAction(FavoritesAction.OpenDetail(bird.id)) },
                height = piuMosaicHeightFor(index),
                modifier = Modifier.animateItem(),
            )
        }
    }
}

/**
 * Nenhum favorito: o convite para explorar, centralizado no espaço acima da barra flutuante.
 * BoxWithConstraints dá a altura disponível (maxHeight), usada para centralizar o conteúdo e ainda
 * permitir rolagem com fonte grande.
 */
@Composable
private fun EmptyFavorites(
    onAction: (FavoritesAction) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier,
) {
    Column(modifier.fillMaxSize().padding(bottom = contentPadding.calculateBottomPadding())) {
        FavoritesHeader(count = null, Modifier.padding(horizontal = PiuDimens.screenMargin))
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            PiuStateScreen(
                icon = painterResource(DesignRes.drawable.ic_favorites),
                title = stringResource(Res.string.empty_title),
                body = stringResource(Res.string.empty_body),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                        .padding(vertical = 24.dp),
            ) {
                Button(onClick = { onAction(FavoritesAction.ExploreClick) }) {
                    Text(stringResource(Res.string.explore_birds))
                }
            }
        }
    }
}
