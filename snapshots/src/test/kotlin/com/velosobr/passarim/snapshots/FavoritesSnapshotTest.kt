// Snapshots da aba Favoritos.

package com.velosobr.passarim.snapshots

import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.feature.favorites.presentation.FavoritesScreen
import com.velosobr.passarim.feature.favorites.presentation.FavoritesState
import org.junit.Test

/**
 * O mosaico com as quatro aves da prancheta e o estado vazio. A 2.0× a grade fica em 1 coluna. A
 * ordem da lista é a que dá o desenho da prancheta: a grade põe cada card na coluna mais curta, então
 * a terceira ave (alta) vai para a direita, embaixo do Carcará, e a quarta (baixa) para a esquerda.
 */
class FavoritesSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private val birds =
        listOf(
            SpeciesSummary("jabiru-mycteria", "Tuiuiú", "Jabiru mycteria", null, Biome.PANTANAL),
            SpeciesSummary("caracara-plancus", "Carcará", "Caracara plancus", null, Biome.CAATINGA),
            SpeciesSummary("anodorhynchus-hyacinthinus", "Arara-azul-grande", "Anodorhynchus hyacinthinus", null, Biome.PANTANAL),
            SpeciesSummary("cariama-cristata", "Seriema", "Cariama cristata", null, Biome.CERRADO),
        )

    @Test
    fun list() = snap("favorites") { FavoritesScreen(state = FavoritesState.Content(birds), onAction = {}) }

    @Test
    fun empty() = snap("favorites-empty") { FavoritesScreen(state = FavoritesState.Empty, onAction = {}) }
}
