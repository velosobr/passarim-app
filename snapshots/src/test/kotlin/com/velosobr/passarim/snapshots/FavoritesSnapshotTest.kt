package com.velosobr.passarim.snapshots

import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.feature.favorites.presentation.FavoritesScreen
import com.velosobr.passarim.feature.favorites.presentation.FavoritesState
import org.junit.Test

class FavoritesSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private val birds =
        listOf(
            SpeciesSummary("anodorhynchus-hyacinthinus", "Arara-azul-grande", "Anodorhynchus hyacinthinus", null),
            SpeciesSummary("ramphastos-toco", "Tucano-toco", "Ramphastos toco", null),
            SpeciesSummary("saltator-similis", "Trinca-ferro", "Saltator similis", null),
        )

    @Test
    fun list() = snap("favorites") { FavoritesScreen(state = FavoritesState.Content(birds), onAction = {}) }

    @Test
    fun empty() = snap("favorites-empty") { FavoritesScreen(state = FavoritesState.Empty, onAction = {}) }
}
