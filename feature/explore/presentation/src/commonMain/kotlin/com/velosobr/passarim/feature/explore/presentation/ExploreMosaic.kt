// O mosaico do Explorar (frente E): a "Ave do dia" em largura total e os cards
// de espécie em colunas desencontradas, com o rodapé de "carregando mais".
// Separado da ExploreScreen para cada arquivo ter uma responsabilidade.

package com.velosobr.passarim.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.feature.explore.presentation.resources.Res
import com.velosobr.passarim.feature.explore.presentation.resources.featured_badge
import com.velosobr.passarim.feature.explore.presentation.resources.featured_badge_biome
import com.velosobr.passarim.feature.explore.presentation.resources.featured_play
import com.velosobr.passarim.feature.explore.presentation.resources.load_more_error
import com.velosobr.passarim.feature.explore.presentation.resources.retry
import com.velosobr.piuds.components.PiuSkeletonBox
import com.velosobr.piuds.components.adult.PiuFeaturedCard
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.components.adult.PiuMosaicHeight
import com.velosobr.piuds.components.adult.PiuPlayAction
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource

/**
 * A altura do card na posição [index] do mosaico: alto, baixo, baixo, alto, e repete. Com duas
 * colunas, a da esquerda começa alta e a da direita baixa, e as linhas nunca se alinham.
 */
fun mosaicHeightFor(index: Int): PiuMosaicHeight = if (index % 4 == 0 || index % 4 == 3) PiuMosaicHeight.TALL else PiuMosaicHeight.SHORT

/** O gradiente de um card sem bioma principal (catalog sem esse dado): o do Pantanal. */
private val FallbackBiome = Biome.PANTANAL

/**
 * O mosaico: a "Ave do dia" ocupando a largura toda e, embaixo, os cards em colunas desencontradas,
 * com o rodapé de "carregando mais" ou de erro. LazyVerticalStaggeredGrid é a grade em que cada item
 * tem a própria altura e entra na coluna mais curta; FullLine faz um item ocupar todas as colunas.
 * snapshotFlow transforma a leitura do estado da grade num Flow, e rememberUpdatedState dá ao efeito
 * o total atual de itens sem reiniciá-lo.
 */
@Composable
internal fun SpeciesMosaic(
    state: ExploreState,
    columns: Int,
    onAction: (ExploreAction) -> Unit,
    contentPadding: PaddingValues,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    // Pede a próxima página quando o último item visível chega perto do fim (a regra está em shouldLoadMore;
    // o ViewModel ignora pedidos repetidos, então emitir a mais é inofensivo).
    val currentOnAction by rememberUpdatedState(onAction)
    LaunchedEffect(gridState) {
        snapshotFlow {
            val info = gridState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: -1) to info.totalItemsCount
        }.distinctUntilChanged()
            .collect { (last, count) -> if (shouldLoadMore(last, count)) currentOnAction(ExploreAction.LoadMore) }
    }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding.plusMargins(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
    ) {
        val featured = state.featured
        if (state.showFeaturedSlot) {
            // A mesma chave "featured" para o esqueleto e para o card: a grade troca um pelo outro no lugar.
            item(key = "featured", span = StaggeredGridItemSpan.FullLine, contentType = "featured") {
                if (featured != null) {
                    FeaturedCard(featured.species, featured.hasSong, state, onAction)
                } else {
                    PiuSkeletonBox(Modifier.fillMaxWidth().height(196.dp), shape = RoundedCornerShape(28.dp))
                }
            }
        }
        itemsIndexed(state.items, key = { _, bird -> bird.id }, contentType = { _, _ -> "species" }) { index, bird ->
            PiuMosaicCard(
                name = bird.commonName,
                scientificName = bird.scientificName,
                imageUrl = bird.thumbnailUrl,
                biome = bird.primaryBiome ?: FallbackBiome,
                isFavorite = bird.id in state.favoriteIds,
                onToggleFavorite = { onAction(ExploreAction.ToggleFavorite(bird)) },
                onClick = { onAction(ExploreAction.OpenDetail(bird.id)) },
                height = mosaicHeightFor(index),
            )
        }
        if (state.isLoadingMore || state.loadMoreError != null) {
            item(key = "footer", span = StaggeredGridItemSpan.FullLine, contentType = "footer") {
                LoadMoreFooter(isLoading = state.isLoadingMore, onRetry = { onAction(ExploreAction.RetryLoadMore) })
            }
        }
    }
}

/** O card da "Ave do dia", com o selo citando o bioma principal (quando os filtros já trazem o nome). */
@Composable
private fun FeaturedCard(
    bird: com.velosobr.passarim.core.domain.SpeciesSummary,
    hasSong: Boolean,
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
) {
    val biomeLabel =
        (state.filters as? FiltersPhase.Loaded)
            ?.filters
            ?.biomes
            ?.find { it.code == bird.primaryBiome?.code }
            ?.label
    PiuFeaturedCard(
        badge =
            if (biomeLabel != null) {
                stringResource(Res.string.featured_badge_biome, biomeLabel.uppercase())
            } else {
                stringResource(Res.string.featured_badge)
            },
        name = bird.commonName,
        scientificName = bird.scientificName,
        imageUrl = bird.thumbnailUrl,
        biome = bird.primaryBiome ?: FallbackBiome,
        onClick = { onAction(ExploreAction.OpenFeatured) },
        play =
            if (hasSong) {
                PiuPlayAction(
                    description = stringResource(Res.string.featured_play, bird.commonName),
                    playing = false,
                    onClick = { onAction(ExploreAction.PlayFeatured) },
                )
            } else {
                null
            },
    )
}

/** As margens laterais da tela somadas ao espaço da barra flutuante embaixo. */
@Composable
private fun PaddingValues.plusMargins(): PaddingValues {
    val direction = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(direction) + 16.dp,
        end = calculateEndPadding(direction) + 16.dp,
        bottom = calculateBottomPadding() + 16.dp,
    )
}

/** Fim da grade: carregando a próxima página, ou a linha de erro com "Tentar novamente". */
@Composable
private fun LoadMoreFooter(
    isLoading: Boolean,
    onRetry: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                stringResource(Res.string.load_more_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
        }
    }
}
