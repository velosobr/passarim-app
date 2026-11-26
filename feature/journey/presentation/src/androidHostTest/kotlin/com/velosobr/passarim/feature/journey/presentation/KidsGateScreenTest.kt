// Testes da tela da trava para adultos: a pergunta por extenso (lida dos
// recursos do Compose), o que o leitor de tela anuncia e o que ele não deve
// ler (a resposta digitada), o teclado próprio e a rolagem com fonte 2.0×.
// Rodam no Robolectric com runComposeUiTest (veja FavoritesScreenTest).

package com.velosobr.passarim.feature.journey.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class KidsGateScreenTest {
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private val isPoliteLiveRegion = SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite)

    private val state = KidsGateState(question = GateQuestion(13, 7), answer = "91")

    @Composable
    private fun Gate(
        state: KidsGateState,
        onAction: (KidsGateAction) -> Unit = {},
    ) = PiuTheme(journey = Journey.KIDS) { KidsGateScreen(state, onAction) }

    @Test
    fun `a pergunta sai por extenso e e anunciada em liveRegion`() =
        runComposeUiTest {
            setContent { Gate(state) }
            onNodeWithText("Só para adultos").assertExists()
            onNodeWithText("Quanto é treze vezes sete?").assert(isPoliteLiveRegion)
        }

    @Test
    fun `numeros de mais de uma palavra tambem saem dos recursos`() =
        runComposeUiTest {
            setContent { Gate(KidsGateState(question = GateQuestion(87, 4))) }
            onNodeWithText("Quanto é oitenta e sete vezes quatro?").assertExists()
        }

    @Test
    fun `o erro aparece em liveRegion so depois de errar`() =
        runComposeUiTest {
            var current by mutableStateOf(state)
            setContent { Gate(current) }
            onNodeWithText("Ops, essa não. Tente esta!").assertDoesNotExist()
            current = KidsGateState(question = GateQuestion(18, 6), wrongAnswer = true)
            onNodeWithText("Ops, essa não. Tente esta!").assert(isPoliteLiveRegion)
            onNodeWithText("Quanto é dezoito vezes seis?").assertExists()
        }

    @Test
    fun `a resposta digitada fica fora da arvore de acessibilidade`() =
        runComposeUiTest {
            setContent { Gate(state) }
            // A árvore "mesclada" é a que o leitor de tela percorre: nela, nenhum nó tem o texto da resposta.
            onNodeWithText("91", substring = true).assertDoesNotExist()
        }

    @Test
    fun `as teclas sao botoes de 64 dp e apagar e confirmar tem rotulo`() =
        runComposeUiTest {
            val actions = mutableListOf<KidsGateAction>()
            setContent { Gate(state, onAction = { actions += it }) }
            onNodeWithText("7").assert(hasRole(Role.Button)).assertHeightIsAtLeast(64.dp).performClick()
            onNodeWithText("0").performScrollTo().performClick()
            onNodeWithContentDescription(
                "Apagar",
            ).performScrollTo().assert(hasRole(Role.Button)).assertHeightIsAtLeast(64.dp).performClick()
            onNodeWithContentDescription("Confirmar").performScrollTo().assert(hasRole(Role.Button)).performClick()
            assertThat(actions).containsExactly(
                KidsGateAction.TypeDigit(7),
                KidsGateAction.TypeDigit(0),
                KidsGateAction.Erase,
                KidsGateAction.Confirm,
            )
        }

    @Test
    fun `voltar e um botao que fecha a trava`() =
        runComposeUiTest {
            val actions = mutableListOf<KidsGateAction>()
            setContent { Gate(state, onAction = { actions += it }) }
            onNode(hasText("Voltar")).assert(hasRole(Role.Button)).assertHeightIsAtLeast(56.dp).performClick()
            assertThat(actions).containsExactly(KidsGateAction.Back)
        }

    @Test
    fun `com fonte 2_0 a tela rola ate o confirmar`() =
        runComposeUiTest {
            val actions = mutableListOf<KidsGateAction>()
            setContent {
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                    Gate(state, onAction = { actions += it })
                }
            }
            onNode(hasScrollAction()).assertExists()
            onNodeWithContentDescription("Confirmar").performScrollTo().performClick()
            assertThat(actions).containsExactly(KidsGateAction.Confirm)
        }
}
