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
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey
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
}
