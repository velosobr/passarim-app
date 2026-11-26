// Testes da tela da Escolha: os dois cartões como botões, os quatro estados
// do cartão Kids e o alvo de toque de 48 dp do adulto. Rodam no Robolectric
// com runComposeUiTest (veja FavoritesScreenTest).

package com.velosobr.passarim.feature.journey.presentation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class JourneyChoiceScreenTest {
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private val actions = mutableListOf<JourneyChoiceAction>()

    private fun androidx.compose.ui.test.ComposeUiTest.show(kids: KidsAvailabilityState) =
        setContent { PiuTheme { JourneyChoiceScreen(JourneyChoiceState(kids), onAction = { actions += it }) } }

    @Test
    fun `o titulo e cabecalho e o cartao do adulto e um botao de 48 dp`() =
        runComposeUiTest {
            show(KidsAvailabilityState.HIDDEN)
            onNode(hasText("Quem vai explorar as aves?") and isHeading).assertExists()
            onNodeWithText("Dá para trocar depois, nas Configurações.").assertExists()
            onNodeWithText("Passarim")
                .assert(hasRole(Role.Button))
                .assertIsEnabled()
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertThat(actions).containsExactly(JourneyChoiceAction.ChooseJourney(Journey.ADULT))
        }

    @Test
    fun `conferindo o cartao Kids aparece desabilitado`() =
        runComposeUiTest {
            show(KidsAvailabilityState.CHECKING)
            onNodeWithText("Passarim Kids")
                .performScrollTo()
                .assert(hasRole(Role.Button))
                .assertIsNotEnabled()
                .performClick()
            onNodeWithText("Tentar de novo").assertDoesNotExist()
            assertThat(actions).isEmpty()
        }

    @Test
    fun `disponivel o cartao Kids grava KIDS`() =
        runComposeUiTest {
            show(KidsAvailabilityState.AVAILABLE)
            onNodeWithText("Passarim Kids")
                .performScrollTo()
                .assertIsEnabled()
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertThat(actions).containsExactly(JourneyChoiceAction.ChooseJourney(Journey.KIDS))
        }

    @Test
    fun `sem rede o cartao Kids fica desabilitado com o aviso e o tentar de novo`() =
        runComposeUiTest {
            show(KidsAvailabilityState.OFFLINE)
            onNodeWithText("Passarim Kids").performScrollTo().assertIsNotEnabled()
            onNodeWithText("Precisa de internet na primeira vez").performScrollTo().assertExists()
            onNodeWithText("Tentar de novo")
                .performScrollTo()
                .assert(hasRole(Role.Button))
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertThat(actions).containsExactly(JourneyChoiceAction.RetryKids)
        }

    @Test
    fun `BFF sem o Kids esconde o cartao`() =
        runComposeUiTest {
            show(KidsAvailabilityState.HIDDEN)
            onNodeWithText("Passarim Kids").assertDoesNotExist()
            onNodeWithText("Precisa de internet na primeira vez").assertDoesNotExist()
        }
}
