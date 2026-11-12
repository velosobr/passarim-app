// Testes da foto que viaja do card até o topo do Detalhe, com a tela de
// Detalhe de verdade (DetailScreen). Na ida, o Detalhe quase sempre nasce
// carregando (a ave ainda vem da rede), então é o esqueleto que precisa ter a
// mesma chave do card desde o primeiro quadro; sem ela, o card não tem par e a
// foto não viaja.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.presentation.DetailPreview
import com.velosobr.piuds.components.PiuSharedScope
import com.velosobr.piuds.components.PiuSharedTransitionLayout
import com.velosobr.piuds.components.piuSharedPhoto
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp")
class DetailPhotoTransitionTest {
    /** Abre o Detalhe no meio do teste. */
    private var detailOpen by mutableStateOf(false)

    /**
     * Onde o card começa (a borda de cima, em pixels), quadro a quadro, depois de tocar nele. O card
     * fica 300 dp abaixo do topo da tela e tem a chave "foto-1"; o Detalhe abre carregando, com a
     * [preview] que a rota daria. AnimatedContent troca as duas telas como o NavHost faz por dentro,
     * e mainClock.autoAdvance = false deixa o teste avançar o relógio um quadro de cada vez. Com
     * par, o card viaja junto até o topo do Detalhe; sem par, só esmaece no lugar.
     */
    private fun cardTopsAfterOpening(preview: DetailPreview): List<Float> {
        val tops = mutableListOf<Float>()
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                PiuTheme {
                    PiuSharedTransitionLayout {
                        AnimatedContent(
                            targetState = detailOpen,
                            modifier = Modifier.fillMaxSize(),
                            transitionSpec = { (fadeIn() togetherWith fadeOut()).using(null) },
                        ) { open ->
                            PiuSharedScope(this) {
                                if (open) {
                                    DetailScreen(state = DetailState(phase = DetailPhase.Loading, preview = preview), onAction = {})
                                } else {
                                    Box(
                                        Modifier
                                            .offset(40.dp, 300.dp)
                                            .size(100.dp)
                                            .piuSharedPhoto("foto-1")
                                            .background(Color.Red)
                                            .testTag("card")
                                            .clickable { detailOpen = true },
                                    )
                                }
                            }
                        }
                    }
                }
            }
            mainClock.advanceTimeBy(1_000)
            onNodeWithTag("card").performClick()
            repeat(40) {
                mainClock.advanceTimeByFrame()
                onAllNodes(hasTestTag("card")).fetchSemanticsNodes().forEach { tops += it.boundsInRoot.top }
            }
        }
        return tops
    }

    @Test
    fun `aberto carregando o esqueleto recebe a foto do card`() {
        // Em algum quadro o card está no meio do caminho até o topo: ele achou o par no esqueleto.
        assertThat(cardTopsAfterOpening(DetailPreview(photoKey = "foto-1")).any { it > 20 && it < 280 }).isTrue()
    }

    @Test
    fun `sem a chave na rota o card nao viaja`() {
        assertThat(cardTopsAfterOpening(DetailPreview()).any { it > 20 && it < 280 }).isFalse()
    }
}
