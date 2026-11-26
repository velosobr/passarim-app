// A forma de onda do canto no player do Detalhe: barras verticais com a
// altura de cada trecho do áudio, e a parte já tocada na cor primária. É um
// componente do Detalhe, e não do PiuDS, porque só esta tela o usa (a "regra
// do 2" do PiuDS: um componente só sobe para o design system quando a
// segunda tela pede). A conta das barras é uma função pura (waveformBars),
// testada sem desenhar nada; o desenho só pinta o que ela devolve.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Quantas barras a forma de onda tem: uma para cada um dos 64 picos que o catalog calcula. */
const val WAVEFORM_BARS = 64

/**
 * A altura mínima de uma barra (fração da altura total). Um trecho em silêncio (pico 0) ainda
 * aparece como um traço, e sem picos todas as barras ficam nessa altura: uma linha reta.
 */
const val MIN_BAR_HEIGHT = 0.1f

/** Uma barra: [height] de [MIN_BAR_HEIGHT] a 1 (fração da altura total) e se o canto já passou por ela. */
data class WaveformBar(
    val height: Float,
    val played: Boolean,
)

/**
 * Transforma os [peaks] do canto e o [progress] do player (de 0 a 1) nas [barCount] barras da
 * forma de onda. Com outra quantidade de picos, a lista é dividida em [barCount] trechos iguais
 * e cada barra fica com o maior pico do trecho dela (com mais picos que barras, nenhum trecho alto
 * some; com menos, cada pico se estica por um trecho igual). Uma barra conta como tocada quando o
 * progresso já passou do começo dela por inteiro: com progresso 0 nenhuma acende, com 1 todas.
 */
fun waveformBars(
    peaks: List<Float>,
    progress: Float,
    barCount: Int = WAVEFORM_BARS,
): List<WaveformBar> {
    val playedCount = playedBarCount(progress, barCount)
    return waveformHeights(peaks, barCount).mapIndexed { index, height -> WaveformBar(height, played = index < playedCount) }
}

/**
 * Só as alturas das [barCount] barras (a parte de waveformBars que depende dos [peaks]). O desenho
 * as guarda enquanto os picos não mudam, porque o progresso muda a cada quadro e os picos não.
 */
fun waveformHeights(
    peaks: List<Float>,
    barCount: Int = WAVEFORM_BARS,
): List<Float> =
    List(barCount) { index ->
        val peak =
            if (peaks.isEmpty()) {
                0f
            } else {
                // O trecho da barra: do pico "from" (inclusive) ao "to" (exclusive), com pelo menos um pico.
                val from = index * peaks.size / barCount
                val to = maxOf(from + 1, (index + 1) * peaks.size / barCount)
                peaks.subList(from, to).max()
            }
        peak.coerceIn(MIN_BAR_HEIGHT, 1f)
    }

/** Quantas das [barCount] barras o canto já passou por inteiro, com o [progress] limitado de 0 a 1. */
fun playedBarCount(
    progress: Float,
    barCount: Int = WAVEFORM_BARS,
): Int = (progress.coerceIn(0f, 1f) * barCount).toInt()

/** A altura do desenho das barras, como na prancheta. */
private val WaveformDrawingHeight = 32.dp

/**
 * A altura mínima da forma de onda inteira: ela é o controle de posição do canto, e 48 dp é a
 * altura mínima de um alvo de toque no Android. O desenho de 32 dp fica centralizado nela.
 */
private val WaveformTouchHeight = 48.dp

/**
 * Desenha a forma de onda: as barras centralizadas na vertical, com cantos arredondados e um
 * espaço entre elas. O [modifier] (com os gestos de tocar e arrastar) vale para a caixa de 48 dp,
 * e não só para o desenho: assim o dedo acerta a forma de onda mesmo um pouco acima ou abaixo
 * dela. Canvas é a área de desenho livre do Compose: o bloco roda a cada quadro em
 * que algo que ele lê muda (aqui, o progresso); as alturas das barras, que só dependem dos picos,
 * ficam guardadas de um quadro para o outro. A parte tocada usa [playedColor] (a primária do tema) e o resto
 * [idleColor] (outlineVariant, o cinza das bordas).
 */
@Composable
fun Waveform(
    peaks: List<Float>,
    progress: Float,
    modifier: Modifier = Modifier,
    playedColor: Color = MaterialTheme.colorScheme.primary,
    idleColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Box(modifier.fillMaxWidth().heightIn(min = WaveformTouchHeight), contentAlignment = Alignment.Center) {
        WaveformBars(peaks, progress, playedColor, idleColor)
    }
}

/** O desenho das barras em si, com [WaveformDrawingHeight] de altura. */
@Composable
private fun WaveformBars(
    peaks: List<Float>,
    progress: Float,
    playedColor: Color,
    idleColor: Color,
) {
    // As alturas só mudam com os picos: remember as guarda entre as recomposições, e o desenho de cada
    // quadro só conta as barras tocadas.
    val heights = remember(peaks) { waveformHeights(peaks) }
    Canvas(Modifier.fillMaxWidth().height(WaveformDrawingHeight)) {
        val playedCount = playedBarCount(progress, heights.size)
        val gap = 2.dp.toPx()
        val barWidth = ((size.width - gap * (heights.size - 1)) / heights.size).coerceAtLeast(1f)
        val radius = CornerRadius(barWidth / 2)
        heights.forEachIndexed { index, height ->
            val barHeight = size.height * height
            drawRoundRect(
                color = if (index < playedCount) playedColor else idleColor,
                topLeft = Offset(index * (barWidth + gap), (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = radius,
            )
        }
    }
}
