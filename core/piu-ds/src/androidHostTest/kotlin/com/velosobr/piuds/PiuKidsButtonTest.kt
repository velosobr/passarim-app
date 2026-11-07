// Testes do botão do Kids: é um botão com o alvo de 56 dp do Kids, não
// responde desabilitado e, ao ser pressionado, a face afunda até cobrir a
// sombra: com a mola de toque do PiuMotion, ou de uma vez com "reduzir
// animações" (o "teste de estado" da spec, seção 8).

package com.velosobr.piuds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.kids.PiuKidsButton
import com.velosobr.piuds.theme.LocalPiuReduceMotion
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuKidsButtonTest {
    @Test
    fun `botao do Kids e um botao com alvo de 56 dp`() =
        runComposeUiTest {
            var clicks = 0
            setContent { PiuTheme(journey = Journey.KIDS) { PiuKidsButton("Ouvir!", onClick = { clicks++ }) } }
            onNodeWithText("Ouvir!")
                .assertHasClickAction()
                .assertIsEnabled()
                .assertHeightIsAtLeast(56.dp)
                .performClick()
            assertThat(clicks).isEqualTo(1)
        }

    @Test
    fun `botao do Kids desabilitado nao responde`() =
        runComposeUiTest {
            var clicks = 0
            setContent { PiuTheme(journey = Journey.KIDS) { PiuKidsButton("Ouvir!", onClick = { clicks++ }, enabled = false) } }
            onNodeWithText("Ouvir!").assertIsNotEnabled().performClick()
            assertThat(clicks).isEqualTo(0)
        }

    /**
     * A posição do topo do rótulo antes do toque (o primeiro valor) e depois, quadro a quadro,
     * enquanto o dedo segura o botão (down sem up). mainClock.autoAdvance = false para o relógio só
     * andar quando o teste mandar. A base é medida antes do down: com a troca seca, a face já pode
     * estar embaixo no primeiro quadro depois do toque.
     */
    private fun labelTopsWhilePressed(reduceMotion: Boolean): List<Float> {
        var tops = emptyList<Float>()
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                CompositionLocalProvider(LocalPiuReduceMotion provides reduceMotion) {
                    PiuTheme(journey = Journey.KIDS) { PiuKidsButton("Ouvir!", onClick = {}) }
                }
            }
            mainClock.advanceTimeBy(1_000)
            val rest = onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot().top.value
            onNodeWithText("Ouvir!").performTouchInput { down(center) }
            tops = listOf(rest) +
                List(30) {
                    mainClock.advanceTimeByFrame()
                    onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot().top.value
                }
        }
        return tops
    }

    @Test
    fun `com reduzir animacoes a face afunda de uma vez`() {
        val tops = labelTopsWhilePressed(reduceMotion = true)
        // A sombra do botão é de 5 dp (buttonShadow dos tokens Kids): é o quanto a face desce.
        assertThat(tops.last() - tops.first()).isCloseTo(5f, 0.5f)
        assertThat(tops.distinct()).containsExactly(tops.first(), tops.last())
    }

    @Test
    fun `sem reduzir animacoes a face afunda aos poucos`() {
        val tops = labelTopsWhilePressed(reduceMotion = false)
        assertThat(tops.last() - tops.first()).isCloseTo(5f, 0.5f)
        assertThat(tops.any { it > tops.first() && it < tops.last() }).isTrue()
    }

    @Test
    fun `ao soltar a face volta ao lugar mesmo passando do ponto com a mola`() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent { PiuTheme(journey = Journey.KIDS) { PiuKidsButton("Ouvir!", onClick = {}) } }
            mainClock.advanceTimeBy(1_000)
            val rest = onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot().top.value
            onNodeWithText("Ouvir!").performTouchInput { down(center) }
            repeat(30) { mainClock.advanceTimeByFrame() }
            onNodeWithText("Ouvir!").performTouchInput { up() }
            // Quadro a quadro: a mola que passa do ponto na volta é o caso que derrubava o app.
            repeat(60) {
                mainClock.advanceTimeByFrame()
                onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot()
            }
            assertThat(onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot().top.value).isCloseTo(rest, 0.5f)
        }

    @Test
    fun `um toque rapido dentro de uma lista rolavel tambem afunda a face`() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            // Dentro de algo rolável, o clickable só avisa o "pressionado" depois de ter certeza de que
            // não é o começo de uma rolagem; num toque rápido, o aviso e a soltura chegam juntos.
            setContent {
                PiuTheme(journey = Journey.KIDS) {
                    Column(Modifier.verticalScroll(rememberScrollState())) { PiuKidsButton("Ouvir!", onClick = {}) }
                }
            }
            mainClock.advanceTimeBy(1_000)
            val rest = onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot().top.value
            onNodeWithText("Ouvir!").performClick()
            // 90 quadros (1,5 s): a descida inteira, a volta e o balanço da mola até assentar.
            val tops =
                List(90) {
                    mainClock.advanceTimeByFrame()
                    onNodeWithText("Ouvir!", useUnmergedTree = true).getBoundsInRoot().top.value
                }
            assertThat(tops.any { it > rest + 1f }).isTrue()
            assertThat(tops.last()).isCloseTo(rest, 0.5f)
        }
}
