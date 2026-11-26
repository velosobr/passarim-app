// Testes de interface do Explorar no host (Robolectric): o título e a busca
// fazem parte da grade e sobem junto com ela ao rolar, continuam na tela em
// todas as fases (carregando, vazio, erro) e o campo de busca não perde o
// foco quando a fase muda enquanto a pessoa digita.

package com.velosobr.passarim.feature.explore.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExploreScrollTest {
    /** Trinta aves: bastante para a grade rolar várias telas. */
    private val birds =
        List(30) { SpeciesSummary("ave-$it", "Ave $it", "Avis numerus $it", null, Biome.CERRADO) }

    /**
     * A grade é o único nó que rola na vertical e aceita "rolar até o item N" (a linha de chips
     * também aceita, mas rola na horizontal). VerticalScrollAxisRange é a propriedade de
     * semântica que diz até onde algo rola na vertical.
     */
    private val grid = hasScrollToIndexAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)

    @Test
    fun `ao rolar a grade o titulo e a busca saem da tela`() =
        runComposeUiTest {
            setContent {
                PiuTheme { ExploreScreen(ExploreState(items = birds, phase = ExplorePhase.Content), onAction = {}) }
            }
            onNodeWithText("Explorar").assertIsDisplayed()
            onNode(grid).performScrollToIndex(25)
            // A grade preguiçosa descarta os itens que saem da tela: o título e a busca somem da árvore.
            onNodeWithText("Explorar").assertDoesNotExist()
            onNode(hasSetTextAction()).assertDoesNotExist()
        }

    @Test
    fun `carregando vazio e erro continuam mostrando o titulo e a busca`() =
        runComposeUiTest {
            var phase: ExplorePhase by mutableStateOf(ExplorePhase.Loading)
            setContent { PiuTheme { ExploreScreen(ExploreState(phase = phase), onAction = {}) } }
            listOf(ExplorePhase.Loading, ExplorePhase.Empty, ExplorePhase.Error(ScreenError.Generic)).forEach {
                phase = it
                waitForIdle()
                onNodeWithText("Explorar").assertIsDisplayed()
                onNode(hasText("Buscar aves")).assertIsDisplayed()
            }
        }

    /**
     * Digitar muda a fase (a busca recomeça: carregando, depois vazio ou com aves). Se o campo
     * saísse da composição nessa troca, o teclado fecharia no meio da palavra.
     */
    @Test
    fun `a busca continua com o foco quando a fase muda ao digitar`() =
        runComposeUiTest {
            setContent {
                var state by remember { mutableStateOf(ExploreState(items = birds, phase = ExplorePhase.Content)) }
                PiuTheme {
                    ExploreScreen(
                        state = state,
                        onAction = { action ->
                            if (action is ExploreAction.QueryChange) {
                                state = state.copy(query = action.text, items = emptyList(), phase = ExplorePhase.Loading)
                            }
                        },
                    )
                }
            }
            onNode(hasSetTextAction()).performClick()
            onNode(hasSetTextAction()).performTextInput("ara")
            onNode(hasSetTextAction()).assertIsFocused()
            onNode(hasText("ara")).assertIsDisplayed()
        }

    /** Prancheta: 18 dp entre o título da tela e a primeira seção (a busca), sem a contagem. */
    @Test
    fun `o titulo fica a 18 dp da busca`() =
        runComposeUiTest {
            setContent { PiuTheme { ExploreScreen(ExploreState(items = birds, phase = ExplorePhase.Content), onAction = {}) } }
            val title = onNode(hasText("Explorar") and isHeading()).getBoundsInRoot()
            val search = onNode(hasSetTextAction()).getBoundsInRoot()
            assertThat(search.top - title.bottom).isEqualTo(18.dp)
        }

    /** Filtros com a contagem por bioma principal: 41 aves no catálogo. */
    private val filtersWith41Birds =
        Filters(biomes = emptyList(), states = emptyList(), primaryBiomes = listOf(BiomeFilter("cerrado", "Cerrado", 41)))

    /** Com a contagem de aves embaixo do título, ela fica colada nele, e a busca a 18 dp dela. */
    @Test
    fun `com a contagem a busca fica a 18 dp dela e a contagem colada no titulo`() =
        runComposeUiTest {
            setContent {
                PiuTheme {
                    ExploreScreen(
                        ExploreState(items = birds, phase = ExplorePhase.Content, filters = FiltersPhase.Loaded(filtersWith41Birds)),
                        onAction = {},
                    )
                }
            }
            val title = onNode(hasText("Explorar") and isHeading()).getBoundsInRoot()
            val count = onNodeWithText("41 aves").getBoundsInRoot()
            val search = onNode(hasSetTextAction()).getBoundsInRoot()
            assertThat(count.top - title.bottom).isEqualTo(0.dp)
            assertThat(search.top - count.bottom).isEqualTo(18.dp)
        }
}
