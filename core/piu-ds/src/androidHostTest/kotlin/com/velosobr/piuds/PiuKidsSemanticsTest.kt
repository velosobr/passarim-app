// Testes de acessibilidade dos componentes do Kids: cada clicável tem papel,
// descrição e o alvo de toque do Kids (56 dp, porque o dedo da criança é
// menos preciso). Rodam no Robolectric com runComposeUiTest (veja
// PiuCommonSemanticsTest).

package com.velosobr.piuds

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.components.kids.PiuKidsNavBar
import com.velosobr.piuds.components.kids.PiuMissingSticker
import com.velosobr.piuds.components.kids.PiuPipo
import com.velosobr.piuds.components.kids.PiuPipoBalloon
import com.velosobr.piuds.components.kids.PiuSticker
import com.velosobr.piuds.components.kids.PiuWorldCard
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuKidsSemanticsTest {
    /** Confere o papel que o leitor de tela anuncia ("botão", "aba"). */
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    /** Qualquer nó com descrição para o leitor de tela. */
    private fun hasAnyContentDescription() = SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription)

    @Composable
    private fun Kids(content: @Composable () -> Unit) = PiuTheme(journey = Journey.KIDS, content = content)

    @Test
    fun `cartao de mundo e um botao que le o nome e o progresso`() =
        runComposeUiTest {
            var opened = 0
            setContent { Kids { PiuWorldCard(Biome.PAMPA, "Pampa", onClick = { opened++ }, progress = "2 de 3") } }
            onNodeWithText("Pampa")
                .assert(hasRole(Role.Button))
                .assert(hasText("2 de 3"))
                .assertHasClickAction()
                .assertHeightIsAtLeast(56.dp)
                .performClick()
            assertThat(opened).isEqualTo(1)
        }

    @Test
    fun `figurinha guardada e um botao com o nome da ave`() =
        runComposeUiTest {
            var opened = 0
            setContent { Kids { PiuSticker("Tuiuiú", Biome.PANTANAL, imageUrl = null, onClick = { opened++ }) } }
            onNodeWithText("Tuiuiú").assert(hasRole(Role.Button)).assertHeightIsAtLeast(56.dp).performClick()
            assertThat(opened).isEqualTo(1)
        }

    @Test
    fun `figurinha que falta e lida como uma frase so e nao e clicavel`() =
        runComposeUiTest {
            setContent { Kids { PiuMissingSticker(label = "Ainda falta", description = "figurinha que falta") } }
            onNodeWithContentDescription("figurinha que falta").assertHasNoClickAction()
            // clearAndSetSemantics troca o "?" e o rótulo pela descrição: o leitor de tela não lê o "?".
            onNodeWithText("?").assertDoesNotExist()
        }

    @Test
    fun `balao do Pipo le so a frase e o Pipo e decoracao`() =
        runComposeUiTest {
            setContent { Kids { PiuPipoBalloon("Qual ave vamos conhecer hoje?") } }
            onNodeWithText("Qual ave vamos conhecer hoje?").assertExists()
            // Nenhum nó com descrição: o desenho do Pipo não é anunciado.
            onAllNodes(hasAnyContentDescription(), useUnmergedTree = true).assertCountEquals(0)
        }

    @Test
    fun `itens da barra do Kids sao abas de 56 dp sempre com o rotulo`() =
        runComposeUiTest {
            var chosen = -1
            val items = listOf(PiuNavItem(PiuPipo, "Explorar"), PiuNavItem(PiuPipo, "Álbum"))
            setContent { Kids { PiuKidsNavBar(items, selected = 0, onSelect = { chosen = it }) } }
            onNodeWithText("Explorar").assert(hasRole(Role.Tab)).assertIsSelected().assertHeightIsAtLeast(56.dp)
            onNodeWithText("Álbum")
                .assert(hasRole(Role.Tab))
                .assertIsNotSelected()
                .assertHeightIsAtLeast(56.dp)
                .assertWidthIsAtLeast(56.dp)
                .performClick()
            assertThat(chosen).isEqualTo(1)
        }
}
