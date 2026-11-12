// Os itens da grade do Explorar (frente E): a "Ave do dia" em largura total,
// os cards de espécie em colunas desencontradas, o rodapé de "carregando
// mais" e o esqueleto do carregamento. A ExploreScreen monta a grade e põe
// estes itens embaixo do título, da busca e dos chips; separar os dois
// arquivos dá a cada um uma responsabilidade.

package com.velosobr.passarim.feature.explore.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.presentation.speciesPhotoKey
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
 * Pede a próxima página quando o último item visível chega perto do fim da grade (a regra está em
 * shouldLoadMore; o ViewModel ignora pedidos repetidos e fora da fase de conteúdo, então emitir a
 * mais é inofensivo). snapshotFlow transforma a leitura do estado da grade num Flow, e
 * rememberUpdatedState dá ao efeito o onAction atual sem reiniciá-lo.
 */
@Composable
internal fun LoadMoreEffect(
    gridState: LazyStaggeredGridState,
    onAction: (ExploreAction) -> Unit,
) {
    val currentOnAction by rememberUpdatedState(onAction)
    LaunchedEffect(gridState) {
        snapshotFlow {
            val info = gridState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: -1) to info.totalItemsCount
        }.distinctUntilChanged()
            .collect { (last, count) -> if (shouldLoadMore(last, count)) currentOnAction(ExploreAction.LoadMore) }
    }
}

/**
 * Os itens do mosaico: a "Ave do dia" ocupando a largura toda e, embaixo, os cards em colunas
 * desencontradas, com o rodapé de "carregando mais" ou de erro. Na LazyVerticalStaggeredGrid cada
 * item tem a própria altura e entra na coluna mais curta; FullLine faz um item ocupar todas as
 * colunas. É uma função de extensão de LazyStaggeredGridScope (o "escopo" em que se declaram os
 * itens de uma grade), para a ExploreScreen chamá-la dentro da grade dela.
 */
internal fun LazyStaggeredGridScope.mosaicItems(
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
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
            // A foto do card viaja até o topo do Detalhe ao abrir a ave (só o mosaico: a "Ave do dia"
            // costuma estar também no mosaico, e duas fotos com a mesma chave deixariam a origem ambígua).
            sharedPhotoKey = speciesPhotoKey(bird.id),
        )
    }
    if (state.isLoadingMore || state.loadMoreError != null) {
        item(key = "footer", span = StaggeredGridItemSpan.FullLine, contentType = "footer") {
            LoadMoreFooter(isLoading = state.isLoadingMore, onRetry = { onAction(ExploreAction.RetryLoadMore) })
        }
    }
}

/** O "esqueleto" do carregamento: quatro blocos cinzas no desenho do mosaico, no lugar dos cards. */
internal fun LazyStaggeredGridScope.skeletonItems() {
    items(4, key = { "skeleton-$it" }, contentType = { "skeleton" }) { index ->
        val height = if (mosaicHeightFor(index) == PiuMosaicHeight.TALL) 220.dp else 168.dp
        PiuSkeletonBox(Modifier.fillMaxWidth().height(height), shape = MaterialTheme.shapes.large)
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
internal fun PaddingValues.plusMargins(): PaddingValues {
    val direction = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(direction) + GridMargin,
        end = calculateEndPadding(direction) + GridMargin,
        bottom = calculateBottomPadding() + 16.dp,
    )
}

/** A margem lateral da grade, que [fullBleed] desfaz para os itens do topo. */
private val GridMargin = 16.dp

/**
 * Faz um item ocupar a largura da tela inteira, por cima da margem lateral da grade. O título, a
 * busca e os chips já trazem as próprias margens, e a linha de chips precisa ir até a borda para
 * rolar na horizontal sem um corte a 16 dp dela. Modifier.layout mede o item com a largura da
 * grade mais as duas margens e o desenha deslocado para a esquerda; a grade não corta o que passa
 * da área dos itens enquanto continua dentro dela.
 */
internal fun Modifier.fullBleed(): Modifier =
    layout { measurable, constraints ->
        val extra = (GridMargin * 2).roundToPx()
        val width = constraints.maxWidth + extra
        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
        layout(constraints.maxWidth, placeable.height) { placeable.place(-extra / 2, 0) }
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
