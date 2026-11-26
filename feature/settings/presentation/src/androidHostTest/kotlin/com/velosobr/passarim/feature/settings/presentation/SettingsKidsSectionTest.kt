// Testes da seção "Para crianças" das Configurações (frente D): aparece no
// topo quando o BFF tem o Kids, troca de subtítulo sem rede e fica
// desabilitada enquanto confere. Rodam no Robolectric com runComposeUiTest.

package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isLessThanOrEqualTo
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SettingsKidsSectionTest {
    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private val state = SettingsState(Settings(), dynamicColorsSupported = true, versionName = "1.0.0")

    @Test
    fun `com o Kids disponivel a secao para criancas vem antes da aparencia`() =
        runComposeUiTest {
            val actions = mutableListOf<SettingsAction>()
            setContent { PiuTheme { SettingsScreen(state.copy(kids = KidsAvailabilityState.AVAILABLE), onAction = { actions += it }) } }
            val kidsSection = onNode(hasText("Para crianças") and isHeading).getBoundsInRoot()
            val appearance = onNode(hasText("Aparência") and isHeading).getBoundsInRoot()
            assertThat(kidsSection.top).isLessThanOrEqualTo(appearance.top)
            onNodeWithText("Abre a versão para crianças. Para voltar, um adulto resolve uma conta.").assertExists()
            onNodeWithText("Passarim Kids")
                .assert(hasRole(Role.Button))
                .assertIsEnabled()
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertThat(actions).containsExactly(SettingsAction.OpenKids)
        }

    @Test
    fun `sem rede o subtitulo vira precisa de internet e o toque confere de novo`() =
        runComposeUiTest {
            val actions = mutableListOf<SettingsAction>()
            setContent { PiuTheme { SettingsScreen(state.copy(kids = KidsAvailabilityState.OFFLINE), onAction = { actions += it }) } }
            onNodeWithText("Precisa de internet").assertExists()
            onNodeWithText("Passarim Kids").assertIsEnabled().performClick()
            assertThat(actions).containsExactly(SettingsAction.OpenKids)
        }

    @Test
    fun `conferindo o item Kids aparece desabilitado`() =
        runComposeUiTest {
            setContent { PiuTheme { SettingsScreen(state.copy(kids = KidsAvailabilityState.CHECKING), onAction = {}) } }
            onNodeWithText("Passarim Kids").assertIsNotEnabled()
        }
}
