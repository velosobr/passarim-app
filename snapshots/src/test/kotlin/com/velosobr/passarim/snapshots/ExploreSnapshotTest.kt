// Snapshots da tela Explorar.

package com.velosobr.passarim.snapshots

import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.domain.StateFilter
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.explore.presentation.ExplorePhase
import com.velosobr.passarim.feature.explore.presentation.ExploreScreen
import com.velosobr.passarim.feature.explore.presentation.ExploreState
import com.velosobr.passarim.feature.explore.presentation.FiltersPhase
import com.velosobr.passarim.feature.explore.presentation.FiltersSheet
import com.velosobr.passarim.feature.explore.presentation.FiltersSheetState
import org.junit.Test

/**
 * As telas de Explorar dos mockups: grade, carregando, vazio, erros e o sheet de filtros. A 2.0× a grade
 * fica em 1 coluna. O sheet é fotografado sem o `ModalBottomSheet` (o Paparazzi não desenha janelas).
 */
class ExploreSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private val birds =
        listOf(
            SpeciesSummary("arara-azul-grande", "Arara-azul-grande", "Anodorhynchus hyacinthinus", null),
            SpeciesSummary("bem-te-vi", "Bem-te-vi", "Pitangus sulphuratus", null),
            SpeciesSummary("sabia-laranjeira", "Sabiá-laranjeira", "Turdus rufiventris", null),
            SpeciesSummary("tucano-toco", "Tucano-toco", "Ramphastos toco", null),
        )

    private val filters =
        Filters(
            biomes =
                listOf(
                    BiomeFilter("cerrado", "Cerrado", 26),
                    BiomeFilter("mata_atlantica", "Mata Atlântica", 29),
                    BiomeFilter("amazonia", "Amazônia", 24),
                    BiomeFilter("caatinga", "Caatinga", 19),
                    BiomeFilter("pantanal", "Pantanal", 19),
                    BiomeFilter("pampa", "Pampa", 15),
                ),
            states =
                listOf("SP" to 29, "PR" to 24, "RS" to 24, "RJ" to 23, "GO" to 22, "BA" to 21, "AM" to 19, "PA" to 19)
                    .plus(listOf("MT" to 19, "MG" to 19, "MS" to 16, "SC" to 15, "AC" to 3))
                    .map { (code, count) -> StateFilter(code, count) },
        )

    private fun screen(
        name: String,
        state: ExploreState,
    ) = snap(name) { ExploreScreen(state = state, onAction = {}) }

    @Test
    fun grid() =
        screen(
            "grid",
            ExploreState(items = birds, phase = ExplorePhase.Content, favoriteIds = setOf("arara-azul-grande", "tucano-toco")),
        )

    @Test
    fun loading() = screen("loading", ExploreState(phase = ExplorePhase.Loading))

    @Test
    fun empty() =
        screen(
            "empty",
            ExploreState(query = "arara", biome = "pampa", phase = ExplorePhase.Empty, filters = FiltersPhase.Loaded(filters)),
        )

    @Test
    fun errorNoConnection() = screen("error-no-connection", ExploreState(phase = ExplorePhase.Error(ScreenError.NoConnection)))

    @Test
    fun errorServer() = screen("error-server", ExploreState(phase = ExplorePhase.Error(ScreenError.ServiceUnavailable)))

    @Test
    fun filtersSheet() =
        snap("filters-sheet") {
            FiltersSheet(sheet = FiltersSheetState(biome = "cerrado", state = "RJ"), filters = FiltersPhase.Loaded(filters), onAction = {})
        }
}
