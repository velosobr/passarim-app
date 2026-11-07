// Testes de acessibilidade dos componentes comuns do PiuDS. A "árvore de
// semântica" é o que o TalkBack e o VoiceOver leem: os testes a consultam
// por texto, descrição ou papel (botão, aba), como um leitor de tela faria.
// runComposeUiTest monta a composição de verdade no Robolectric.

package com.velosobr.piuds

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuCommonSemanticsTest {
    /** Confere o papel que o leitor de tela anuncia ("botão", "aba"). */
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    @Test
    fun `credito com link e um botao que abre a fonte`() =
        runComposeUiTest {
            var opened = 0
            setContent { PiuTheme { PiuCreditCaption("Foto: Fulano", onOpenSource = { opened++ }) } }
            onNodeWithText("Foto: Fulano").assert(hasRole(Role.Button)).assertHasClickAction().performClick()
            assertThat(opened).isEqualTo(1)
        }

    @Test
    fun `credito com o link desligado nao e clicavel`() =
        runComposeUiTest {
            setContent {
                PiuTheme(journey = Journey.KIDS) { PiuCreditCaption("Foto: Fulano", onOpenSource = {}, linkEnabled = false) }
            }
            onNodeWithText("Foto: Fulano").assertHasNoClickAction()
        }

    @Test
    fun `credito sem fonte continua so texto`() =
        runComposeUiTest {
            setContent { PiuTheme { PiuCreditCaption("Canto: Beltrano") } }
            onNodeWithText("Canto: Beltrano").assertHasNoClickAction()
        }
}
