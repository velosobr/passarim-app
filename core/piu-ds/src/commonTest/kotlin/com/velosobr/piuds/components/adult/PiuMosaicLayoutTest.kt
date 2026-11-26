// Testes das regras de layout do mosaico do PiuDS: quantas colunas para cada
// escala de fonte e a sequência de alturas que deixa as colunas desencontradas.
// São funções puras (sem Compose), então rodam como teste comum.

package com.velosobr.piuds.components.adult

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.piuds.components.adult.PiuMosaicHeight.SHORT
import com.velosobr.piuds.components.adult.PiuMosaicHeight.TALL
import kotlin.test.Test

class PiuMosaicLayoutTest {
    @Test
    fun `duas colunas ate fonte 1_3 e uma coluna a partir de 1_5`() {
        assertThat(piuMosaicColumns(1.0f)).isEqualTo(2)
        assertThat(piuMosaicColumns(1.3f)).isEqualTo(2)
        assertThat(piuMosaicColumns(PIU_STACK_FONT_SCALE)).isEqualTo(1)
        assertThat(piuMosaicColumns(2.0f)).isEqualTo(1)
    }

    @Test
    fun `as alturas alternam alto e baixo para as colunas ficarem desencontradas`() {
        // A grade põe cada card na coluna mais curta: alto (esquerda), baixo (direita), alto (direita,
        // que ficou mais curta), baixo (esquerda). Cada coluna fica com um alto e um baixo e as duas
        // terminam juntas, como na prancheta; FavoritesScreenTest confere isso na tela de verdade.
        val heights = (0 until 8).map { piuMosaicHeightFor(it) }
        assertThat(heights).isEqualTo(listOf(TALL, SHORT, TALL, SHORT, TALL, SHORT, TALL, SHORT))
    }
}
