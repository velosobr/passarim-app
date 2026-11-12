// Testes da tela de Favoritos no mosaico (frente E3): o título e a contagem no
// singular e no plural, os toques no card e no coração, o título rolando junto
// com a grade e o estado vazio. Rodam no Robolectric (um Android de mentira na
// JVM) com runComposeUiTest, que monta a tela e deixa consultar a semântica.

package com.velosobr.passarim.feature.favorites.presentation

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private val arara = SpeciesSummary("anodorhynchus-hyacinthinus", "Arara-azul-grande", "Anodorhynchus hyacinthinus", null, Biome.PANTANAL)
private val carcara = SpeciesSummary("caracara-plancus", "Carcará", "Caracara plancus", null, Biome.CAATINGA)

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FavoritesScreenTest {
    /** O título é marcado como cabeçalho: o leitor de tela deixa pular direto para ele. */
    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    @Test
    fun `titulo e cabecalho e a contagem fica no plural`() =
        runComposeUiTest {
            setContent { PiuTheme { FavoritesScreen(FavoritesState.Content(listOf(arara, carcara)), onAction = {}) } }
            onNodeWithText("Favoritos").assert(isHeading)
            onNodeWithText("2 aves guardadas").assertIsDisplayed()
        }

    @Test
    fun `com uma ave a contagem fica no singular`() =
        runComposeUiTest {
            setContent { PiuTheme { FavoritesScreen(FavoritesState.Content(listOf(arara)), onAction = {}) } }
            onNodeWithText("1 ave guardada").assertIsDisplayed()
        }

    @Test
    fun `tocar no card abre a ave e tocar no coracao desfavorita`() =
        runComposeUiTest {
            val actions = mutableListOf<FavoritesAction>()
            setContent { PiuTheme { FavoritesScreen(FavoritesState.Content(listOf(arara, carcara)), onAction = { actions += it }) } }
            onNodeWithText("Carcará").performClick()
            onNode(hasContentDescription("Desfavoritar Arara-azul-grande")).performClick()
            assertThat(actions).containsExactly(
                FavoritesAction.OpenDetail(carcara.id),
                FavoritesAction.ToggleFavorite(arara),
            )
        }

    @Test
    fun `o titulo rola junto com a grade`() =
        runComposeUiTest {
            val many = (1..16).map { SpeciesSummary("ave-$it", "Ave $it", "Avis $it", null, Biome.CERRADO) }
            setContent { PiuTheme { FavoritesScreen(FavoritesState.Content(many), onAction = {}) } }
            onNode(hasScrollAction()).performScrollToNode(hasText("Ave 16"))
            // Se o título ficasse fixo no topo, ele continuaria na tela depois de rolar até o fim.
            assertThat(onNodeWithText("Favoritos").isNotDisplayed()).isTrue()
        }

    @Test
    fun `vazio convida a explorar`() =
        runComposeUiTest {
            val actions = mutableListOf<FavoritesAction>()
            setContent { PiuTheme { FavoritesScreen(FavoritesState.Empty, onAction = { actions += it }) } }
            onNodeWithText("Favoritos").assert(isHeading)
            onNodeWithText("Nenhum favorito ainda").assertIsDisplayed()
            onNodeWithText("Explorar aves").performClick()
            assertThat(actions).containsExactly(FavoritesAction.ExploreClick)
        }
}
