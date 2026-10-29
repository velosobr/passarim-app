// O conteúdo do sheet de filtros do Explorar.

package com.velosobr.passarim.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.feature.explore.presentation.resources.Res
import com.velosobr.passarim.feature.explore.presentation.resources.filter_biome
import com.velosobr.passarim.feature.explore.presentation.resources.filter_count
import com.velosobr.passarim.feature.explore.presentation.resources.filter_state
import com.velosobr.passarim.feature.explore.presentation.resources.retry
import com.velosobr.passarim.feature.explore.presentation.resources.sheet_apply
import com.velosobr.passarim.feature.explore.presentation.resources.sheet_clear
import com.velosobr.passarim.feature.explore.presentation.resources.sheet_error
import com.velosobr.passarim.feature.explore.presentation.resources.sheet_show_all_states
import com.velosobr.passarim.feature.explore.presentation.resources.sheet_title
import com.velosobr.piuds.components.PiuFilterChip
import com.velosobr.piuds.theme.PiuDimens
import org.jetbrains.compose.resources.stringResource

/**
 * Conteúdo do sheet de filtros: um bioma e um estado no máximo (a API aceita um de cada), com a
 * contagem de espécies. As escolhas ficam no [sheet] e só valem em "Mostrar aves". É um componente
 * da feature, não do design system. O `ModalBottomSheet` em volta fica em `ExploreScreen`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersSheet(
    sheet: FiltersSheetState,
    filters: FiltersPhase,
    onAction: (ExploreAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.sheet_title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { onAction(ExploreAction.ClearSheet) }) { Text(stringResource(Res.string.sheet_clear)) }
        }
        when (filters) {
            FiltersPhase.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            FiltersPhase.Error -> {
                Text(stringResource(Res.string.sheet_error), color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { onAction(ExploreAction.RetryFilters) }) { Text(stringResource(Res.string.retry)) }
            }
            is FiltersPhase.Loaded -> {
                Group(stringResource(Res.string.filter_biome)) {
                    filters.filters.biomes.forEach { biome ->
                        PiuFilterChip(
                            label = stringResource(Res.string.filter_count, biome.label, biome.speciesCount),
                            selected = biome.code == sheet.biome,
                            onClick = { onAction(ExploreAction.SheetBiome(biome.code)) },
                        )
                    }
                }
                Group(stringResource(Res.string.filter_state)) {
                    visibleStates(filters.filters.states, sheet.showAllStates).forEach { uf ->
                        PiuFilterChip(
                            label = stringResource(Res.string.filter_count, uf.code, uf.speciesCount),
                            selected = uf.code == sheet.state,
                            onClick = { onAction(ExploreAction.SheetState(uf.code)) },
                        )
                    }
                }
                if (!sheet.showAllStates && filters.filters.states.size > VISIBLE_STATES) {
                    TextButton(onClick = { onAction(ExploreAction.ShowAllStates) }) {
                        Text(stringResource(Res.string.sheet_show_all_states))
                    }
                }
            }
        }
        Button(
            onClick = { onAction(ExploreAction.ApplyFilters) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        ) { Text(stringResource(Res.string.sheet_apply)) }
    }
}

/** Um título com os chips do grupo embaixo, quebrando em linhas. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Group(
    title: String,
    chips: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(PiuDimens.unit)) {
            chips()
        }
    }
}
