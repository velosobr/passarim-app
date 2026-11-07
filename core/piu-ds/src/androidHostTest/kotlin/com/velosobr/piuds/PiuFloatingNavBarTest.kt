// Testes da barra flutuante do adulto: os itens são abas com o alvo de
// 48 dp, e o item ativo cresce com a mola de estado do PiuMotion, ou de uma
// vez com "reduzir animações" (o "teste de estado" da spec, seção 8).

package com.velosobr.piuds

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.components.adult.PiuFloatingNavBar
import com.velosobr.piuds.components.kids.PiuPipo
import com.velosobr.piuds.components.rememberPiuBlurState
import com.velosobr.piuds.theme.LocalPiuReduceMotion
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuFloatingNavBarTest {
    /** Confere o papel que o leitor de tela anuncia ("aba"). */
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    /** Dois destinos com o mesmo ícone: o teste só olha rótulos, papéis e tamanhos. */
    private val navItems = listOf(PiuNavItem(PiuPipo, "Explorar"), PiuNavItem(PiuPipo, "Favoritos"))

    @Test
    fun `itens da barra flutuante sao abas de 48 dp e o inativo e descrito pelo icone`() =
        runComposeUiTest {
            var chosen = -1
            setContent {
                PiuTheme { PiuFloatingNavBar(navItems, selected = 0, onSelect = { chosen = it }, blurState = rememberPiuBlurState()) }
            }
            onNodeWithText("Explorar").assert(hasRole(Role.Tab)).assertIsSelected().assertHeightIsAtLeast(48.dp)
            onNodeWithContentDescription("Favoritos")
                .assertIsNotSelected()
                .assertHeightIsAtLeast(48.dp)
                .assertWidthIsAtLeast(48.dp)
                .performClick()
            assertThat(chosen).isEqualTo(1)
        }

    /**
     * As larguras do item "Favoritos", quadro a quadro, depois de ele virar o ativo.
     * mainClock.autoAdvance = false para o relógio da composição só andar quando o teste mandar:
     * assim dá para ver cada quadro da animação. 30 quadros (meio segundo) bastam para a mola de
     * estado chegar ao fim.
     */
    private fun widthsAfterSelecting(reduceMotion: Boolean): List<Float> {
        var widths = emptyList<Float>()
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                CompositionLocalProvider(LocalPiuReduceMotion provides reduceMotion) {
                    PiuTheme {
                        var selected by remember { mutableIntStateOf(0) }
                        PiuFloatingNavBar(navItems, selected, onSelect = { selected = it }, blurState = rememberPiuBlurState())
                    }
                }
            }
            mainClock.advanceTimeBy(1_000)
            onAllNodes(isSelectable())[1].performClick()
            widths =
                List(30) {
                    mainClock.advanceTimeByFrame()
                    onAllNodes(isSelectable())[1].getBoundsInRoot().width.value
                }
        }
        return widths
    }

    @Test
    fun `com reduzir animacoes o item ativo da barra flutuante cresce de uma vez`() {
        val widths = widthsAfterSelecting(reduceMotion = true)
        assertThat(widths.last()).isGreaterThan(widths.first())
        assertThat(widths.distinct()).containsExactly(widths.first(), widths.last())
    }

    @Test
    fun `sem reduzir animacoes o item ativo da barra flutuante cresce aos poucos`() {
        val widths = widthsAfterSelecting(reduceMotion = false)
        assertThat(widths.last()).isGreaterThan(widths.first())
        assertThat(widths.any { it > widths.first() && it < widths.last() }).isTrue()
    }
}
