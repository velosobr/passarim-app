// Teste do número de colunas da grade por escala de fonte.

package com.velosobr.passarim.feature.explore.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.piuds.components.adult.PiuMosaicHeight.SHORT
import com.velosobr.piuds.components.adult.PiuMosaicHeight.TALL
import kotlin.test.Test

class ExploreLayoutTest {
    @Test
    fun `duas colunas ate fonte 1_3 e uma coluna a partir de 1_5`() {
        assertThat(exploreGridColumns(1.0f)).isEqualTo(2)
        assertThat(exploreGridColumns(1.3f)).isEqualTo(2)
        assertThat(exploreGridColumns(1.5f)).isEqualTo(1)
        assertThat(exploreGridColumns(2.0f)).isEqualTo(1)
    }

    @Test
    fun `o mosaico alterna alto e baixo para as colunas ficarem desencontradas`() {
        // Coluna da esquerda: alto, baixo; da direita: baixo, alto. Assim as linhas nunca se alinham.
        val heights = (0 until 8).map { mosaicHeightFor(it) }
        assertThat(heights).isEqualTo(
            listOf(TALL, SHORT, SHORT, TALL, TALL, SHORT, SHORT, TALL),
        )
    }
}
