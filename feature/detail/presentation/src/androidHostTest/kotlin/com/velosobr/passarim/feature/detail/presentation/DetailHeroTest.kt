// Testes de interface do topo do Detalhe no host (Robolectric): os botões
// sobre a foto (voltar e favoritar) têm 48 dp e dizem o que fazem, o nome
// fica sobre a foto e o resto da página começa abaixo dela.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isLessThanOrEqualTo
import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp")
class DetailHeroTest {
    private val detail = arara.copy(conservation = ConservationStatus.VU)

    /** Monta o Detalhe com o conteúdo pronto; o mapa vira um espaço vazio (o MapLibre não roda no host). */
    private fun androidx.compose.ui.test.ComposeUiTest.showDetail(
        isFavorite: Boolean,
        onAction: (DetailAction) -> Unit = {},
    ) {
        setContent {
            PiuTheme {
                DetailScreen(
                    state = DetailState(phase = DetailPhase.Content(detail), isFavorite = isFavorite),
                    onAction = onAction,
                    mapContent = { _, _ -> },
                )
            }
        }
    }

    @Test
    fun `os botoes sobre a foto tem 48 dp e dizem o que fazem`() =
        runComposeUiTest {
            val actions = mutableListOf<DetailAction>()
            showDetail(isFavorite = false) { actions += it }
            onNodeWithContentDescription("Voltar").assertWidthIsAtLeast(48.dp).assertHeightIsAtLeast(48.dp).performClick()
            onNodeWithContentDescription("Favoritar Arara-azul-grande")
                .assertWidthIsAtLeast(48.dp)
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertThat(actions).containsExactly(DetailAction.Back, DetailAction.ToggleFavorite)
        }

    /** Um botão de liga/desliga: o leitor de tela anuncia se a ave já está nos favoritos. */
    @Test
    fun `favoritar e um botao de liga e desliga`() =
        runComposeUiTest {
            showDetail(isFavorite = true)
            onNode(isToggleable()).assertIsOn()
            onNodeWithContentDescription("Desfavoritar Arara-azul-grande").assertIsOn()
        }

    @Test
    fun `sem favorito o botao fica desligado`() =
        runComposeUiTest {
            showDetail(isFavorite = false)
            onNode(isToggleable()).assertIsOff()
        }

    @Test
    fun `o nome fica sobre a foto e o resto da pagina comeca abaixo dela`() =
        runComposeUiTest {
            showDetail(isFavorite = false)
            assertThat(onNodeWithText(detail.commonName).getBoundsInRoot().bottom).isLessThanOrEqualTo(HERO_HEIGHT)
            assertThat(onNodeWithText(detail.scientificName).getBoundsInRoot().bottom).isLessThanOrEqualTo(HERO_HEIGHT)
            assertThat(onNodeWithText("Vulnerável").getBoundsInRoot().top).isGreaterThanOrEqualTo(HERO_HEIGHT)
        }
}
