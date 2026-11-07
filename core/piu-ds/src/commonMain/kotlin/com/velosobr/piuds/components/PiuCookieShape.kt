// A forma "cookie" do Material 3 Expressive: um círculo com a borda em
// ondas suaves, usada no botão de canto e nas figurinhas. O Material só tem
// as formas prontas (MaterialShapes) em versão alpha, então ela é montada
// aqui com o graphics-shapes, a biblioteca de polígonos arredondados do
// AndroidX que o próprio Material usa por dentro.

package com.velosobr.piuds.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star

/**
 * A forma cookie, com [lobes] ondas (9 é o "cookie 9" do Material). Uma
 * estrela com o raio de dentro perto do de fora e as pontas bem arredondadas
 * vira uma borda ondulada.
 *
 * O Shape é o "molde" que o Compose usa para recortar (clip), pintar o fundo
 * e desenhar a borda: createOutline recebe o tamanho real e devolve o
 * contorno.
 */
class PiuCookieShape(
    lobes: Int = 9,
) : Shape {
    // normalized() deixa o polígono no quadrado de 0 a 1; depois é só esticar até o tamanho.
    private val polygon = RoundedPolygon.star(numVerticesPerRadius = lobes, innerRadius = 0.8f, rounding = CornerRounding(0.4f)).normalized()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        // O graphics-shapes só converte para Path no Android (toPath); no código comum, o contorno é
        // montado das curvas de Bézier cúbicas do polígono: cada uma vai de anchor0 a anchor1,
        // puxada pelos dois pontos de controle.
        val cubics = polygon.cubics
        val path = Path()
        path.moveTo(cubics.first().anchor0X, cubics.first().anchor0Y)
        for (c in cubics) {
            path.cubicTo(c.control0X, c.control0Y, c.control1X, c.control1Y, c.anchor1X, c.anchor1Y)
        }
        path.close()
        path.transform(Matrix().apply { scale(size.width, size.height) })
        return Outline.Generic(path)
    }

    // Duas formas com o mesmo número de ondas são iguais: o Compose não refaz o contorno à toa.
    private val lobeCount = lobes

    override fun equals(other: Any?): Boolean = other is PiuCookieShape && other.lobeCount == lobeCount

    override fun hashCode(): Int = lobeCount
}
