// Testes de interface do player do Detalhe no host (Robolectric): o botão de
// tocar em forma de cookie e a forma de onda, que também serve para pular a
// posição do canto (com o dedo ou pelo leitor de tela).

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AudioPlayerCardTest {
    /** Um canto de 31 s, pausado no meio (15,5 s). */
    private val duration = 31_000L

    @Test
    fun `o botao de tocar tem 48 dp e diz o que faz`() =
        runComposeUiTest {
            var toggled = 0
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Idle, { toggled++ }, {})
                }
            }
            onNodeWithContentDescription("Tocar canto").assertWidthIsAtLeast(48.dp).assertHeightIsAtLeast(48.dp).performClick()
            assertThat(toggled).isEqualTo(1)
        }

    @Test
    fun `a forma de onda diz ao leitor de tela quanto do canto ja tocou`() =
        runComposeUiTest {
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Paused(15_500), {}, {})
                }
            }
            onNodeWithContentDescription("Progresso do canto")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(0.5f, 0f..1f)))
        }

    /**
     * A forma de onda é o controle de posição do canto: a área de toque dela tem pelo menos 48 dp
     * de altura (a mínima de um alvo de toque no Android), mesmo com o desenho de 32 dp.
     */
    @Test
    fun `a forma de onda tem area de toque de 48 dp`() =
        runComposeUiTest {
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Paused(0), {}, {})
                }
            }
            onNodeWithContentDescription("Progresso do canto").assertHeightIsAtLeast(48.dp)
        }

    @Test
    fun `o leitor de tela pula a posicao do canto pela forma de onda`() =
        runComposeUiTest {
            var seekMs: Long? = null
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Paused(0), {}, { seekMs = it })
                }
            }
            onNodeWithContentDescription("Progresso do canto").performSemanticsAction(SemanticsActions.SetProgress) { it(0.25f) }
            assertThat(seekMs).isEqualTo(7_750L)
        }

    @Test
    fun `tocar perto do fim da forma de onda pula para perto do fim do canto`() =
        runComposeUiTest {
            var seekMs: Long? = null
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Paused(0), {}, { seekMs = it })
                }
            }
            onNodeWithContentDescription("Progresso do canto").performTouchInput { click(Offset(width * 0.75f, height / 2f)) }
            assertThat(seekMs).isNotNull().isBetween(22_000L, 24_500L)
        }

    /** O que a forma de onda diz ao leitor de tela: a fração do canto que ela mostra como tocada. */
    private fun progressOf(fraction: Float) =
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(fraction, 0f..1f))

    @Test
    fun `arrastar na forma de onda segue o dedo e pula ao soltar`() =
        runComposeUiTest {
            var seekMs: Long? = null
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Paused(0), {}, { seekMs = it })
                }
            }
            val waveform = onNodeWithContentDescription("Progresso do canto")
            waveform.performTouchInput {
                down(Offset(width * 0.25f, height / 2f))
                moveTo(Offset(width * 0.5f, height / 2f))
                moveTo(Offset(width * 0.75f, height / 2f))
            }
            // Enquanto o dedo arrasta, a forma de onda mostra a posição dele, e o player ainda não pulou.
            waveform.assert(progressOf(0.75f))
            assertThat(seekMs).isNull()
            waveform.performTouchInput { up() }
            assertThat(seekMs).isNotNull().isBetween(22_000L, 24_500L)
        }

    /**
     * Um arrasto cancelado (outro gesto, como a rolagem da tela, toma o dedo) não pula a posição, e
     * a forma de onda volta a mostrar onde o player está, em vez de ficar presa onde o dedo parou.
     */
    @Test
    fun `arrasto cancelado devolve a forma de onda a posicao do player`() =
        runComposeUiTest {
            var seekMs: Long? = null
            setContent {
                PiuTheme {
                    AudioPlayerCard(duration, List(64) { 0.5f }, "Gravação: autor", PlaybackState.Paused(0), {}, { seekMs = it })
                }
            }
            val waveform = onNodeWithContentDescription("Progresso do canto")
            waveform.performTouchInput {
                down(Offset(width * 0.25f, height / 2f))
                moveTo(Offset(width * 0.5f, height / 2f))
                moveTo(Offset(width * 0.75f, height / 2f))
                cancel()
            }
            waveform.assert(progressOf(0f))
            assertThat(seekMs).isNull()
        }
}
