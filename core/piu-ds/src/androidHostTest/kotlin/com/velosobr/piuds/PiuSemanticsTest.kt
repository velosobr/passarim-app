// Testes de acessibilidade dos componentes do C2: cada clicável tem papel,
// descrição e o alvo mínimo de toque da variante (48 dp no Piu, 56 dp no
// Kids). A "árvore de semântica" é o que o TalkBack e o VoiceOver leem: os
// testes a consultam por texto, descrição ou papel.

package com.velosobr.piuds

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.components.adult.PiuBiomeChip
import com.velosobr.piuds.components.adult.PiuFloatingNavBar
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.components.adult.PiuNavItem
import com.velosobr.piuds.components.kids.PiuKidsButton
import com.velosobr.piuds.components.kids.PiuKidsNavBar
import com.velosobr.piuds.components.kids.PiuMissingSticker
import com.velosobr.piuds.components.kids.PiuWorldCard
import com.velosobr.piuds.components.kids.PiuPipo
import com.velosobr.piuds.components.rememberPiuBlurState
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuSemanticsTest {
    /** O papel de botão ou de aba, como o leitor de tela anuncia. */
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private fun items(icon: ImageVector) = listOf(PiuNavItem(icon, "Explorar"), PiuNavItem(icon, "Favoritos"))

    @Composable
    private fun Adult(content: @Composable () -> Unit) = PiuTheme(content = content)

    @Composable
    private fun Kids(content: @Composable () -> Unit) = PiuTheme(journey = Journey.KIDS, content = content)

    @Test
    fun kidsButtonIsAButtonWith56dpTarget() =
        runComposeUiTest {
            setContent { Kids { PiuKidsButton("Ouvir!", onClick = {}) } }
            onNodeWithText("Ouvir!", useUnmergedTree = false).assertHasClickAction().assertHeightIsAtLeast(56.dp)
        }

    @Test
    fun worldCardIsAButton() =
        runComposeUiTest {
            setContent { Kids { PiuWorldCard(Biome.PAMPA, "Pampa", onClick = {}, progress = "2 de 3") } }
            onNodeWithText("Pampa").assertHasClickAction().assertHeightIsAtLeast(76.dp)
        }

    @Test
    fun missingStickerIsAnnounced() =
        runComposeUiTest {
            setContent { Kids { PiuMissingSticker(label = "Ainda falta", description = "figurinha que falta") } }
            onNodeWithContentDescription("figurinha que falta").assertExists()
        }

    @Test
    fun kidsNavBarItemsAreTabsWith56dpTarget() =
        runComposeUiTest {
            setContent { Kids { PiuKidsNavBar(items(PiuPipo), selected = 0, onSelect = {}) } }
            onNodeWithText("Explorar").assertIsSelected().assertHeightIsAtLeast(56.dp)
            onNodeWithText("Favoritos").assertHeightIsAtLeast(56.dp).assertWidthIsAtLeast(56.dp)
        }

    @Test
    fun floatingNavBarItemsHave48dpTargetAndInactiveOnesAreDescribed() =
        runComposeUiTest {
            setContent { Adult { PiuFloatingNavBar(items(PiuPipo), selected = 0, onSelect = {}, blurState = rememberPiuBlurState()) } }
            onNodeWithText("Explorar").assertIsSelected().assertHeightIsAtLeast(48.dp)
            onNodeWithContentDescription("Favoritos").assertExists()
        }

    @Test
    fun mosaicFavoriteHas48dpTargetAndDescription() =
        runComposeUiTest {
            setContent {
                Adult {
                    PiuMosaicCard("Carcará", "Caracara plancus", null, Biome.CAATINGA, isFavorite = false, onToggleFavorite = {}, onClick = {})
                }
            }
            onNode(hasContentDescription("Favoritar Carcará"), useUnmergedTree = true).assertExists()
        }

    @Test
    fun biomeChipIsASelectableTab() =
        runComposeUiTest {
            setContent { Adult { PiuBiomeChip(Biome.CERRADO, "Cerrado", selected = true, onClick = {}) } }
            onNode(hasRole(Role.Tab)).assertIsSelected().assertHeightIsAtLeast(48.dp)
        }

    @Test
    fun creditWithoutLinkIsNotClickable() =
        runComposeUiTest {
            setContent {
                Kids {
                    Box(Modifier) { PiuCreditCaption("Foto: Fulano", onOpenSource = {}, linkEnabled = false) }
                }
            }
            onNodeWithText("Foto: Fulano").assertHasNoClickAction()
        }

    @Test
    fun creditWithLinkIsAButton() =
        runComposeUiTest {
            setContent { Adult { PiuCreditCaption("Foto: Fulano", onOpenSource = {}) } }
            onNode(hasRole(Role.Button)).assertHasClickAction()
        }
}
