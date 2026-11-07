// Converte um PiuGradient (cores e ângulo dos tokens) num pincel do Compose.
// O ângulo segue a regra do CSS, a mesma das pranchetas: 0° aponta para
// cima e o ângulo cresce no sentido horário (135° vai do canto de cima à
// esquerda para o de baixo à direita).

package com.velosobr.piuds.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * O pincel do degradê. É um ShaderBrush porque a reta do degradê depende do
 * tamanho de quem é pintado: o Compose chama createShader com o tamanho
 * real, e a reta atravessa a área inteira, como no CSS.
 */
fun PiuGradient.toBrush(): Brush =
    object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val radians = angleDegrees * PI / 180.0
            // Direção do degradê: no CSS, 0° é "para cima" (y negativo na tela).
            val dx = sin(radians).toFloat()
            val dy = -cos(radians).toFloat()
            // Comprimento da reta no CSS: a projeção do retângulo na direção.
            val half = (abs(size.width * dx) + abs(size.height * dy)) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            return LinearGradientShader(
                from = center - Offset(dx * half, dy * half),
                to = center + Offset(dx * half, dy * half),
                colors = listOf(from, to),
            )
        }
    }
