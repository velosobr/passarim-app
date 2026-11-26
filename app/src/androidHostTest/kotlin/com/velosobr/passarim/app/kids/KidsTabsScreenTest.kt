// Testes da casca do Kids: as duas abas com o Pipo, a troca de aba e o botão
// "Para adultos", que abre a trava. Rodam no Robolectric com runComposeUiTest.

package com.velosobr.passarim.app.kids

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class KidsTabsScreenTest {
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    @Test
    fun `abre no Explorar com o Pipo dizendo que logo tem aves`() =
        runComposeUiTest {
            setContent { PiuTheme(journey = Journey.KIDS) { KidsTabsScreen(onOpenAdults = {}) } }
            onNode(hasText("Explorar") and isHeading).assertExists()
            onNode(hasText("Explorar") and hasRole(Role.Tab)).assertIsSelected()
            onNodeWithText("Logo, logo tem aves aqui!").assertExists()
        }

    @Test
    fun `tocar no Album troca de aba`() =
        runComposeUiTest {
            setContent { PiuTheme(journey = Journey.KIDS) { KidsTabsScreen(onOpenAdults = {}) } }
            onNode(hasText("Álbum") and hasRole(Role.Tab)).performClick()
            onNode(hasText("Álbum") and hasRole(Role.Tab)).assertIsSelected()
            onNode(hasText("Explorar") and hasRole(Role.Tab)).assertIsNotSelected()
            onNode(hasText("Álbum") and isHeading).assertExists()
            onNodeWithText("Logo, logo tem aves aqui!").assertExists()
        }

    @Test
    fun `Para adultos e um botao de 56 dp que abre a trava`() =
        runComposeUiTest {
            var opened = 0
            setContent { PiuTheme(journey = Journey.KIDS) { KidsTabsScreen(onOpenAdults = { opened++ }) } }
            onNodeWithText("Para adultos").assert(hasRole(Role.Button)).assertHeightIsAtLeast(56.dp).performClick()
            assertThat(opened).isEqualTo(1)
        }
}
