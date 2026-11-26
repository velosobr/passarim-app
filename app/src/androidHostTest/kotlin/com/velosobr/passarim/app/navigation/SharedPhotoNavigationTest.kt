// Teste da foto que viaja entre os dois NavHosts do app, no host (Robolectric).
// O card do mosaico mora no NavHost das abas (TabsScreen), e o Detalhe no
// NavHost da raiz (App.kt): a foto precisa atravessar de um para o outro na ida
// (abrir o Detalhe) e voltar ao card na volta. O teste monta a mesma ligação do
// app (a camada PiuSharedTransitionLayout em volta do NavHost da raiz, o
// PiuSharedScope com o escopo de cada rota da raiz e as abas dentro da rota
// delas), com telas mínimas no lugar do Explorar e do Detalhe, que precisariam
// do Koin e da rede.

package com.velosobr.passarim.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import assertk.assertThat
import assertk.assertions.isTrue
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
class SharedPhotoNavigationTest {
    /** O NavHost da raiz, guardado para o teste navegar e voltar como os botões do app fazem. */
    private lateinit var rootNav: NavHostController

    /**
     * A ligação do app em miniatura: a raiz com as abas e o Detalhe, e as abas com o próprio
     * NavHost, onde fica o "Explorar" com um card de 100 dp, 300 dp abaixo do topo. O card e o topo
     * do Detalhe (430 dp de altura, no topo da tela) têm a foto da mesma chave.
     */
    @Composable
    private fun AppInMiniature() {
        rootNav = rememberNavController()
        PiuSharedTransitionLayout {
            NavHost(rootNav, startDestination = TabsRoute, modifier = Modifier.fillMaxSize()) {
                composable<TabsRoute> {
                    PiuSharedScope(this) { Tabs(onOpenDetail = { rootNav.navigate(DetailRoute("tuiuiu")) }) }
                }
                composable<DetailRoute> {
                    PiuSharedScope(this) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(430.dp)
                                .piuSharedPhoto("foto-tuiuiu")
                                .background(Color.Blue)
                                .testTag("hero"),
                        )
                    }
                }
            }
        }
    }

    /** As abas: um NavHost interno, como o do TabsScreen, com o "Explorar" e o card. */
    @Composable
    private fun Tabs(onOpenDetail: () -> Unit) {
        val tabsNav = rememberNavController()
        NavHost(tabsNav, startDestination = ExploreRoute, modifier = Modifier.fillMaxSize()) {
            composable<ExploreRoute> {
                Box(
                    Modifier
                        .offset(40.dp, 300.dp)
                        .size(100.dp)
                        .piuSharedPhoto("foto-tuiuiu")
                        .background(Color.Red)
                        .testTag("card")
                        .clickable(onClick = onOpenDetail),
                )
            }
        }
    }

    /**
     * A borda de cima (em pixels) dos nós com a [tag], quadro a quadro, por [frames] quadros. No
     * Robolectric a densidade padrão é 1, então pixels e dp coincidem.
     */
    private fun SemanticsNodeInteractionsProvider.topsFrameByFrame(
        tag: String,
        frames: Int,
        advanceFrame: () -> Unit,
    ): List<Float> {
        val tops = mutableListOf<Float>()
        repeat(frames) {
            advanceFrame()
            onAllNodes(hasTestTag(tag)).fetchSemanticsNodes().forEach { tops += it.boundsInRoot.top }
        }
        return tops
    }

    /**
     * Ida e volta, com o relógio parado (mainClock.autoAdvance = false) e avançado um quadro de cada
     * vez. Na ida, o topo do Detalhe passa pelo meio do caminho entre o card (300) e o topo (0): a
     * foto saiu do card. Na volta, o card passa pelo meio do caminho também: a foto voltou a ele.
     */
    @Test
    fun `a foto viaja do card das abas ao detalhe da raiz e volta`() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent { PiuTheme { AppInMiniature() } }
            mainClock.advanceTimeBy(1_000)

            onNodeWithTag("card").performClick()
            val goingTops = topsFrameByFrame("hero", frames = 60) { mainClock.advanceTimeByFrame() }
            assertThat(goingTops.any { it > 20 && it < 280 }).isTrue()
            mainClock.advanceTimeBy(1_000)

            runOnIdle { rootNav.popBackStack() }
            val returningTops = topsFrameByFrame("card", frames = 60) { mainClock.advanceTimeByFrame() }
            assertThat(returningTops.any { it > 20 && it < 280 }).isTrue()
        }
}
