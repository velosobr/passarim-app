// Testes da reta do degradê: o ângulo segue a regra do CSS (0° para cima,
// sentido horário), a mesma das pranchetas, e a reta atravessa a área toda.
// Função pura, então o teste roda no host e no simulador iOS sem Compose.

package com.velosobr.piuds.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import assertk.assertThat
import assertk.assertions.isCloseTo
import kotlin.test.Test

class GradientLineTest {
    /** Compara os dois pontos com folga de 0,01 px: seno e cosseno não dão números exatos. */
    private fun assertLine(
        actual: Pair<Offset, Offset>,
        from: Offset,
        to: Offset,
    ) {
        assertThat(actual.first.x).isCloseTo(from.x, 0.01f)
        assertThat(actual.first.y).isCloseTo(from.y, 0.01f)
        assertThat(actual.second.x).isCloseTo(to.x, 0.01f)
        assertThat(actual.second.y).isCloseTo(to.y, 0.01f)
    }

    @Test
    fun `0 graus vai de baixo para cima`() {
        assertLine(gradientLine(0f, Size(200f, 100f)), from = Offset(100f, 100f), to = Offset(100f, 0f))
    }

    @Test
    fun `90 graus vai da esquerda para a direita`() {
        assertLine(gradientLine(90f, Size(200f, 100f)), from = Offset(0f, 50f), to = Offset(200f, 50f))
    }

    @Test
    fun `135 graus cruza o quadrado de canto a canto`() {
        assertLine(gradientLine(135f, Size(100f, 100f)), from = Offset(0f, 0f), to = Offset(100f, 100f))
    }

    @Test
    fun `135 graus num card largo chega aos dois cantos como no CSS`() {
        // No CSS, a reta tem o comprimento da projeção do retângulo: as duas pontas do degradê caem
        // nas retas perpendiculares que passam pelos cantos, e os cantos ficam com as cores puras.
        val (from, to) = gradientLine(135f, Size(300f, 100f))
        val dir = Offset(0.70710677f, 0.70710677f)
        assertThat(from.x * dir.x + from.y * dir.y).isCloseTo(0f, 0.01f)
        assertThat(to.x * dir.x + to.y * dir.y).isCloseTo(300f * dir.x + 100f * dir.y, 0.01f)
    }
}
