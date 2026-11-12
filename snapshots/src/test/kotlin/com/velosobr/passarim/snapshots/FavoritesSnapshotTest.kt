// Snapshots da aba Favoritos.

package com.velosobr.passarim.snapshots

import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.feature.favorites.presentation.FavoritesScreen
import com.velosobr.passarim.feature.favorites.presentation.FavoritesState
import org.junit.Test

/** O mosaico com as quatro aves da prancheta e o estado vazio. A 2.0× a grade fica em 1 coluna. */
class FavoritesSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private val birds =
        listOf(
            SpeciesSummary("jabiru-mycteria", "Tuiuiú", "Jabiru mycteria", null, Biome.PANTANAL),
            SpeciesSummary("caracara-plancus", "Carcará", "Caracara plancus", null, Biome.CAATINGA),
            SpeciesSummary("cariama-cristata", "Seriema", "Cariama cristata", null, Biome.CERRADO),
            SpeciesSummary("anodorhynchus-hyacinthinus", "Arara-azul-grande", "Anodorhynchus hyacinthinus", null, Biome.PANTANAL),
        )

    @Test
    fun list() = snap("favorites") { FavoritesScreen(state = FavoritesState.Content(birds), onAction = {}) }

    @Test
    fun empty() = snap("favorites-empty") { FavoritesScreen(state = FavoritesState.Empty, onAction = {}) }
}
