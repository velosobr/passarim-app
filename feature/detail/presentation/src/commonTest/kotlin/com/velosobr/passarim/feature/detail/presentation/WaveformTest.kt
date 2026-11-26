// Testes da função pura da forma de onda: dos picos do canto e do progresso
// do player até as barras que a tela desenha (altura e se já tocou).

package com.velosobr.passarim.feature.detail.presentation

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.each
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.prop
import kotlin.test.Test

class WaveformTest {
    /** 64 picos crescentes, de 0 a 1, como os que o catalog calcula. */
    private val peaks = List(64) { it / 63f }

    @Test
    fun `uma barra por pico com a altura do pico`() {
        val bars = waveformBars(peaks, progress = 0f)
        assertThat(bars).hasSize(WAVEFORM_BARS)
        assertThat(bars[32].height).isEqualTo(peaks[32])
        assertThat(bars.last().height).isEqualTo(1f)
    }

    @Test
    fun `pico zero vira a barra minima para o silencio continuar visivel`() {
        assertThat(waveformBars(peaks, progress = 0f).first().height).isEqualTo(MIN_BAR_HEIGHT)
    }

    @Test
    fun `sem picos a forma de onda e uma linha reta`() {
        val bars = waveformBars(emptyList(), progress = 0.5f)
        assertThat(bars).hasSize(WAVEFORM_BARS)
        assertThat(bars.map { it.height }.distinct()).containsExactly(MIN_BAR_HEIGHT)
    }

    @Test
    fun `com progresso zero nenhuma barra tocou`() {
        assertThat(waveformBars(peaks, progress = 0f)).each { it.prop(WaveformBar::played).isFalse() }
    }

    @Test
    fun `com progresso um todas as barras tocaram`() {
        assertThat(waveformBars(peaks, progress = 1f)).each { it.prop(WaveformBar::played).isTrue() }
    }

    @Test
    fun `no meio do canto a primeira metade das barras tocou`() {
        val bars = waveformBars(peaks, progress = 0.5f)
        assertThat(bars.count { it.played }).isEqualTo(32)
        assertThat(bars[31].played).isTrue()
        assertThat(bars[32].played).isFalse()
    }

    @Test
    fun `progresso fora de 0 a 1 e limitado ao intervalo`() {
        assertThat(waveformBars(peaks, progress = -1f).count { it.played }).isEqualTo(0)
        assertThat(waveformBars(peaks, progress = 2f).count { it.played }).isEqualTo(WAVEFORM_BARS)
    }

    @Test
    fun `picos em quantidade diferente sao reamostrados para o numero de barras`() {
        val bars = waveformBars(listOf(0.2f, 0.8f), progress = 0f)
        assertThat(bars).hasSize(WAVEFORM_BARS)
        assertThat(bars.first().height).isEqualTo(0.2f)
        assertThat(bars.last().height).isEqualTo(0.8f)
    }

    /**
     * Com mais picos que barras, cada barra fica com o maior pico do trecho dela, e não com um pico
     * qualquer: aqui, 128 picos alternando silêncio e máximo viram 64 barras cheias, e nenhum
     * trecho alto some da forma de onda.
     */
    @Test
    fun `com mais picos que barras cada barra pega o maior pico do trecho dela`() {
        val alternating = List(128) { if (it % 2 == 1) 1f else 0f }
        val bars = waveformBars(alternating, progress = 0f)
        assertThat(bars).hasSize(WAVEFORM_BARS)
        assertThat(bars).each { it.prop(WaveformBar::height).isEqualTo(1f) }
    }

    /** Com menos picos que barras, cada pico se estica por um trecho igual: metade das barras para cada um. */
    @Test
    fun `com menos picos que barras cada pico ocupa um trecho igual`() {
        val bars = waveformBars(listOf(0.2f, 0.8f), progress = 0f)
        assertThat(bars.count { it.height == 0.2f }).isEqualTo(WAVEFORM_BARS / 2)
        assertThat(bars.count { it.height == 0.8f }).isEqualTo(WAVEFORM_BARS / 2)
    }

    /**
     * As alturas dependem só dos picos, e o desenho as guarda entre um quadro e outro; a cada quadro
     * só se conta quantas barras já tocaram. As duas contas juntas dão as mesmas barras de waveformBars.
     */
    @Test
    fun `alturas e barras tocadas separadas dao as mesmas barras`() {
        val heights = waveformHeights(peaks)
        val played = playedBarCount(progress = 0.5f, barCount = heights.size)
        assertThat(heights.mapIndexed { index, height -> WaveformBar(height, index < played) })
            .isEqualTo(waveformBars(peaks, progress = 0.5f))
        assertThat(played).isEqualTo(32)
    }
}
