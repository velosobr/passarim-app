// A tela Explorar (frente E): título com o total de aves, busca, chips de
// bioma, a "Ave do dia" e o mosaico de espécies, mais os estados de
// carregando, vazio e erro. Os componentes visuais vêm do PiuDS.

package com.velosobr.passarim.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.biomeOf
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.explore.presentation.resources.Res
import com.velosobr.passarim.feature.explore.presentation.resources.chip_all
import com.velosobr.passarim.feature.explore.presentation.resources.clear_filters
import com.velosobr.passarim.feature.explore.presentation.resources.empty_body_filters
import com.velosobr.passarim.feature.explore.presentation.resources.empty_body_query
import com.velosobr.passarim.feature.explore.presentation.resources.empty_title
import com.velosobr.passarim.feature.explore.presentation.resources.error_generic_body
import com.velosobr.passarim.feature.explore.presentation.resources.error_generic_title
import com.velosobr.passarim.feature.explore.presentation.resources.error_no_connection_body
import com.velosobr.passarim.feature.explore.presentation.resources.error_no_connection_title
import com.velosobr.passarim.feature.explore.presentation.resources.error_rate_limited_body
import com.velosobr.passarim.feature.explore.presentation.resources.error_rate_limited_title
import com.velosobr.passarim.feature.explore.presentation.resources.error_server_body
import com.velosobr.passarim.feature.explore.presentation.resources.error_server_title
import com.velosobr.passarim.feature.explore.presentation.resources.explore_title
import com.velosobr.passarim.feature.explore.presentation.resources.filter_state
import com.velosobr.passarim.feature.explore.presentation.resources.retry
import com.velosobr.passarim.feature.explore.presentation.resources.search_placeholder
import com.velosobr.passarim.feature.explore.presentation.resources.see_favorites
import com.velosobr.passarim.feature.explore.presentation.resources.species_count
import com.velosobr.piuds.components.PiuFilterChip
import com.velosobr.piuds.components.PiuScreenTitle
import com.velosobr.piuds.components.PiuSearchField
import com.velosobr.piuds.components.PiuSkeletonBox
import com.velosobr.piuds.components.PiuStateScreen
import com.velosobr.piuds.components.StateTone
import com.velosobr.piuds.components.adult.PiuBiomeChip
import com.velosobr.piuds.components.adult.PiuMosaicHeight
import com.velosobr.piuds.components.adult.piuMosaicHeightFor
import com.velosobr.piuds.resources.ic_cloud_off
import com.velosobr.piuds.resources.ic_search
import com.velosobr.piuds.resources.ic_wifi_off
import com.velosobr.piuds.theme.PiuDimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.velosobr.piuds.resources.Res as DesignRes

/** A partir desta escala de fonte, a grade passa a 1 coluna: empilhar, nunca cortar o texto. */
const val SINGLE_COLUMN_FONT_SCALE = 1.5f

/** Quantas colunas a grade tem para a escala de fonte do aparelho. */
fun exploreGridColumns(fontScale: Float): Int = if (fontScale >= SINGLE_COLUMN_FONT_SCALE) 1 else 2

/**
 * Explorar, sem estado próprio: tudo vem de [state] e cada interação vira uma [ExploreAction].
 * O título, a busca e os chips ficam visíveis em todas as fases, para que digitar nunca seja
 * interrompido. [contentPadding] é o espaço que a barra flutuante ocupa embaixo: a grade rola por
 * baixo dela (o desfoque precisa do conteúdo atrás) e termina acima dela.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val columns = exploreGridColumns(LocalDensity.current.fontScale)
    Column(modifier.fillMaxSize()) {
        Header(state)
        PiuSearchField(
            value = state.query,
            onValueChange = { onAction(ExploreAction.QueryChange(it)) },
            placeholder = stringResource(Res.string.search_placeholder),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        BiomeChips(state, onAction)
        when (val phase = state.phase) {
            ExplorePhase.Loading -> SkeletonGrid(columns)
            ExplorePhase.Content -> SpeciesMosaic(state, columns, onAction, contentPadding)
            ExplorePhase.Empty -> CenteredState(contentPadding) { EmptyState(state, onAction) }
            is ExplorePhase.Error -> CenteredState(contentPadding) { ErrorState(phase.error, state.retryEnabled, onAction) }
        }
    }
    if (state.sheet != null) {
        ModalBottomSheet(
            onDismissRequest = { onAction(ExploreAction.DismissFilters) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            FiltersSheet(sheet = state.sheet, filters = state.filters, onAction = onAction)
        }
    }
}

/** O título da tela e, quando os filtros já chegaram, o total de aves do catálogo. */
@Composable
private fun Header(state: ExploreState) {
    Column(Modifier.padding(bottom = 8.dp)) {
        PiuScreenTitle(stringResource(Res.string.explore_title), Modifier.padding(bottom = 0.dp))
        state.speciesTotal?.let { total ->
            Text(
                stringResource(Res.string.species_count, total),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = PiuDimens.screenMargin),
            )
        }
    }
}

/**
 * A linha de chips, que rola na horizontal: "Todos", um chip com o gradiente de cada bioma (o toque
 * aplica o filtro direto) e, no fim, o chip de UF, que abre o sheet como antes. LazyRow só cria os
 * chips visíveis, como a grade faz com os cards.
 */
@Composable
private fun BiomeChips(
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
) {
    val biomes = (state.filters as? FiltersPhase.Loaded)?.filters?.biomes.orEmpty()
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "all") {
            PiuFilterChip(
                stringResource(Res.string.chip_all),
                selected = state.biome == null,
                onClick = { onAction(ExploreAction.SelectBiome(null)) },
            )
        }
        // Um bioma que o app não conhece (novo no BFF) fica de fora: sem ele não há gradiente.
        items(biomes.mapNotNull { f -> biomeOf(f.code)?.let { it to f } }, key = { it.second.code }) { (biome, filter) ->
            PiuBiomeChip(
                biome = biome,
                label = filter.label,
                selected = state.biome == filter.code,
                onClick = { onAction(ExploreAction.SelectBiome(filter.code)) },
            )
        }
        item(key = "uf") {
            val uf = state.state
            if (uf == null) {
                PiuFilterChip(
                    stringResource(Res.string.filter_state),
                    selected = false,
                    onClick = { onAction(ExploreAction.OpenFilters) },
                    showDropdown = true,
                )
            } else {
                PiuFilterChip(
                    uf,
                    selected = true,
                    onClick = { onAction(ExploreAction.OpenFilters) },
                    onClear = { onAction(ExploreAction.ClearState) },
                )
            }
        }
    }
}

/** O "esqueleto" do carregamento: quatro blocos cinzas no desenho do mosaico, sem rolagem. */
@Composable
private fun SkeletonGrid(columns: Int) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
        userScrollEnabled = false,
    ) {
        items(4) { index ->
            PiuSkeletonBox(
                Modifier.fillMaxWidth().height(
                    piuMosaicHeightFor(index).let {
                        if (it ==
                            PiuMosaicHeight.TALL
                        ) {
                            220.dp
                        } else {
                            168.dp
                        }
                    },
                ),
                shape = MaterialTheme.shapes.large,
            )
        }
    }
}

/** Nenhuma ave encontrada: o texto cita a busca (se houver) e oferece limpar os filtros. */
@Composable
private fun EmptyState(
    state: ExploreState,
    onAction: (ExploreAction) -> Unit,
) {
    val query = state.query.trim()
    run {
        PiuStateScreen(
            icon = painterResource(DesignRes.drawable.ic_search),
            title = stringResource(Res.string.empty_title),
            body =
                if (query.isNotEmpty()) {
                    stringResource(Res.string.empty_body_query, query)
                } else {
                    stringResource(Res.string.empty_body_filters)
                },
        ) {
            if (state.hasFilters) {
                FilledTonalButton(
                    onClick = { onAction(ExploreAction.ClearFilters) },
                    modifier = Modifier.height(PiuDimens.touchTarget),
                ) {
                    Text(stringResource(Res.string.clear_filters))
                }
            }
        }
    }
}

/** Ícone, título e texto de cada tela de erro. */
private class ErrorCopy(
    val icon: DrawableResource,
    val title: StringResource,
    val body: StringResource,
)

/** Escolhe os textos e o ícone de cada tipo de erro. */
private fun ScreenError.copy(): ErrorCopy =
    when (this) {
        ScreenError.NoConnection ->
            ErrorCopy(DesignRes.drawable.ic_wifi_off, Res.string.error_no_connection_title, Res.string.error_no_connection_body)
        ScreenError.ServiceUnavailable ->
            ErrorCopy(DesignRes.drawable.ic_cloud_off, Res.string.error_server_title, Res.string.error_server_body)
        is ScreenError.RateLimited ->
            ErrorCopy(DesignRes.drawable.ic_cloud_off, Res.string.error_rate_limited_title, Res.string.error_rate_limited_body)
        ScreenError.Generic ->
            ErrorCopy(DesignRes.drawable.ic_cloud_off, Res.string.error_generic_title, Res.string.error_generic_body)
    }

/**
 * Tela de erro com "Tentar novamente" (desligado durante o Retry-After) e,
 * sem conexão, o atalho para os favoritos, que funcionam offline.
 */
@Composable
private fun ErrorState(
    error: ScreenError,
    retryEnabled: Boolean,
    onAction: (ExploreAction) -> Unit,
) {
    val copy = error.copy()
    run {
        PiuStateScreen(
            icon = painterResource(copy.icon),
            title = stringResource(copy.title),
            body = stringResource(copy.body),
            tone = StateTone.Error,
        ) {
            Button(
                onClick = { onAction(ExploreAction.Retry) },
                enabled = retryEnabled,
                modifier = Modifier.height(PiuDimens.touchTarget),
            ) { Text(stringResource(Res.string.retry)) }
            if (error == ScreenError.NoConnection) {
                TextButton(onClick = { onAction(ExploreAction.OpenFavorites) }, modifier = Modifier.height(PiuDimens.touchTarget)) {
                    Text(stringResource(Res.string.see_favorites))
                }
            }
        }
    }
}

/**
 * Centraliza o estado no espaço que sobra (acima da barra flutuante) e rola se a fonte grande não
 * couber na tela.
 */
@Composable
private fun CenteredState(
    contentPadding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize().padding(bottom = contentPadding.calculateBottomPadding()), contentAlignment = Alignment.Center) {
        Box(Modifier.verticalScroll(rememberScrollState()).padding(vertical = 24.dp)) { content() }
    }
}
