// A forma de onda do canto no player do Detalhe: barras verticais com a
// altura de cada trecho do áudio, e a parte já tocada na cor primária. É um
// componente do Detalhe, e não do PiuDS, porque só esta tela o usa (a "regra
// do 2" do PiuDS: um componente só sobe para o design system quando a
// segunda tela pede). A conta das barras é uma função pura (waveformBars),
// testada sem desenhar nada; o desenho só pinta o que ela devolve.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
 * forma de onda. Com outra quantidade de picos, cada barra pega o pico da posição proporcional
 * (a primeira barra o primeiro pico, a última o último). Uma barra conta como tocada quando o
 * progresso já passou do começo dela por inteiro: com progresso 0 nenhuma acende, com 1 todas.
 */
fun waveformBars(
    peaks: List<Float>,
    progress: Float,
    barCount: Int = WAVEFORM_BARS,
): List<WaveformBar> {
    val playedCount = (progress.coerceIn(0f, 1f) * barCount).toInt()
    return List(barCount) { index ->
        val peak =
            if (peaks.isEmpty()) {
                0f
            } else {
                peaks[if (barCount == 1) 0 else index * (peaks.size - 1) / (barCount - 1)]
            }
        WaveformBar(height = peak.coerceIn(MIN_BAR_HEIGHT, 1f), played = index < playedCount)
    }
}

/**
 * Desenha a forma de onda: as barras centralizadas na vertical, com cantos arredondados e um
 * espaço entre elas. Canvas é a área de desenho livre do Compose: o bloco roda a cada quadro em
 * que algo que ele lê muda (aqui, o progresso), e desenhar é barato, então não há nada a guardar
 * entre um quadro e outro. A parte tocada usa [playedColor] (a primária do tema) e o resto
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
    Canvas(modifier.fillMaxWidth().height(32.dp)) {
        val bars = waveformBars(peaks, progress)
        val gap = 2.dp.toPx()
        val barWidth = ((size.width - gap * (bars.size - 1)) / bars.size).coerceAtLeast(1f)
        val radius = CornerRadius(barWidth / 2)
        bars.forEachIndexed { index, bar ->
            val barHeight = size.height * bar.height
            drawRoundRect(
                color = if (bar.played) playedColor else idleColor,
                topLeft = Offset(index * (barWidth + gap), (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = radius,
            )
        }
    }
}
