// Testes da foto que viaja entre duas telas (frente E, seção 6): com a camada
// de transição e o escopo de animação, a foto do card cresce aos poucos até o
// tamanho do topo do Detalhe; com "reduzir animações" ou sem a camada, ela
// não viaja.

package com.velosobr.piuds

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.piuds.components.PiuSharedScope
import com.velosobr.piuds.components.PiuSharedTransitionLayout
import com.velosobr.piuds.components.piuSharedPhoto
import com.velosobr.piuds.theme.LocalPiuReduceMotion
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp")
class PiuSharedPhotoTest {
    /** Abre o "Detalhe" no meio do teste. */
    private var detail by mutableStateOf(false)

    /**
     * Duas telas trocadas por AnimatedContent (o mesmo que o NavHost usa por dentro): um card de
     * 100 dp e um topo de 430 dp de altura, os dois com a foto da chave "foto-1". AnimatedContent
     * dá a cada tela um escopo de animação, que o PiuSharedScope entrega à foto. A troca é só um
     * esmaecer, sem animar o tamanho (SizeTransform nulo) e numa área do tamanho da tela: assim,
     * só a foto compartilhada muda de tamanho no meio da troca.
     */
    @Composable
    private fun TwoScreens() {
        PiuSharedTransitionLayout {
            AnimatedContent(
                targetState = detail,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = { (fadeIn() togetherWith fadeOut()).using(null) },
            ) { showDetail ->
                PiuSharedScope(this) {
                    if (showDetail) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(430.dp)
                                .piuSharedPhoto("foto-1")
                                .background(Color.Blue)
                                .testTag("hero"),
                        )
                    } else {
                        Box(
                            Modifier
                                .offset(
                                    40.dp,
                                    300.dp,
                                ).size(100.dp)
                                .piuSharedPhoto("foto-1")
                                .background(Color.Red)
                                .testTag("card")
                                .clickable {
                                    detail =
                                        true
                                },
                        )
                    }
                }
            }
        }
    }

    /**
     * Onde o topo do Detalhe começa (a borda de cima, em pixels), quadro a quadro, depois de tocar
     * no card. O card começa 300 dp abaixo do topo da tela; o topo do Detalhe, no 0.
     */
    private fun heroTopsAfterOpening(reduceMotion: Boolean): List<Float> {
        val tops = mutableListOf<Float>()
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                CompositionLocalProvider(LocalPiuReduceMotion provides reduceMotion) { PiuTheme { TwoScreens() } }
            }
            mainClock.advanceTimeBy(1_000)
            onNodeWithTag("card").performClick()
            repeat(40) {
                mainClock.advanceTimeByFrame()
                onAllNodes(hasTestTag("hero")).fetchSemanticsNodes().forEach { tops += it.boundsInRoot.top }
            }
        }
        return tops
    }

    @Test
    fun `a foto sai do card e sobe aos poucos ate o topo do detalhe`() {
        // Em algum quadro, a foto está no meio do caminho entre o card e o topo: ela viajou.
        // No Robolectric a densidade padrão é 1, então pixels e dp coincidem.
        assertThat(heroTopsAfterOpening(reduceMotion = false).any { it > 20 && it < 280 }).isTrue()
    }

    @Test
    fun `com reduzir animacoes a foto nao viaja`() {
        assertThat(heroTopsAfterOpening(reduceMotion = true).any { it > 20 && it < 280 }).isFalse()
    }

    @Test
    fun `sem a camada de transicao a foto fica no lugar dela`() =
        runComposeUiTest {
            setContent { PiuTheme { Box(Modifier.size(100.dp).piuSharedPhoto("foto-1").testTag("solo")) } }
            assertThat(onNodeWithTag("solo").fetchSemanticsNode().boundsInRoot.width).isEqualTo(100f)
        }
}
