// Teste do número de colunas da grade por escala de fonte.

package com.velosobr.passarim.feature.explore.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ExploreLayoutTest {
    @Test
    fun `duas colunas ate fonte 1_3 e uma coluna a partir de 1_5`() {
        assertThat(exploreGridColumns(1.0f)).isEqualTo(2)
        assertThat(exploreGridColumns(1.3f)).isEqualTo(2)
        assertThat(exploreGridColumns(1.5f)).isEqualTo(1)
        assertThat(exploreGridColumns(2.0f)).isEqualTo(1)
    }
}
