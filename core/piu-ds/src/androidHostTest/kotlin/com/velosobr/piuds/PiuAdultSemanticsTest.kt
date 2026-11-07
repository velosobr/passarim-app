// Testes de acessibilidade dos componentes da jornada adulta: cada clicável
// tem papel, descrição e o alvo mínimo de toque do Piu (48 dp). Rodam no
// Robolectric com runComposeUiTest (veja PiuCommonSemanticsTest).

package com.velosobr.piuds

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.adult.PiuBiomeChip
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuAdultSemanticsTest {
    /** Confere o papel que o leitor de tela anuncia ("botão", "aba"). */
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    @Composable
    private fun Adult(content: @Composable () -> Unit) = PiuTheme(content = content)

    @Test
    fun `chip de bioma e uma aba marcavel com alvo de 48 dp`() =
        runComposeUiTest {
            var clicks = 0
            setContent {
                Adult {
                    PiuBiomeChip(Biome.CERRADO, "Cerrado", selected = true, onClick = { clicks++ })
                }
            }
            onNodeWithText("Cerrado")
                .assert(hasRole(Role.Tab))
                .assertIsSelected()
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertThat(clicks).isEqualTo(1)
        }

    @Test
    fun `chip de bioma desmarcado e anunciado como nao selecionado`() =
        runComposeUiTest {
            setContent { Adult { PiuBiomeChip(Biome.PAMPA, "Pampa", selected = false, onClick = {}) } }
            onNodeWithText("Pampa").assertIsNotSelected()
        }

    @Test
    fun `card do mosaico e um botao com o nome e o coracao separado`() =
        runComposeUiTest {
            var opened = 0
            var toggled = 0
            setContent {
                Adult {
                    PiuMosaicCard(
                        name = "Carcará",
                        scientificName = "Caracara plancus",
                        imageUrl = null,
                        biome = Biome.CAATINGA,
                        isFavorite = false,
                        onToggleFavorite = { toggled++ },
                        onClick = { opened++ },
                    )
                }
            }
            onNodeWithText("Carcará").assert(hasRole(Role.Button)).assertHasClickAction()
            onNode(hasContentDescription("Favoritar Carcará"))
                .assertIsOff()
                .assertHeightIsAtLeast(48.dp)
                .assertWidthIsAtLeast(48.dp)
                .performClick()
            assertThat(toggled).isEqualTo(1)
            assertThat(opened).isEqualTo(0)
            onNodeWithText("Carcará").performClick()
            assertThat(opened).isEqualTo(1)
        }

    @Test
    fun `coracao de favorita e anunciado como marcado e para desfavoritar`() =
        runComposeUiTest {
            setContent {
                Adult {
                    PiuMosaicCard("Tuiuiú", "Jabiru mycteria", null, Biome.PANTANAL, isFavorite = true, onToggleFavorite = {}, onClick = {})
                }
            }
            onNode(hasContentDescription("Desfavoritar Tuiuiú")).assertIsOn()
        }
}
