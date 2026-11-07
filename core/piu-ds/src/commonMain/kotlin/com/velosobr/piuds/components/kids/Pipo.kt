// O Pipo, o passarinho guia do Kids, como ImageVector: um desenho vetorial
// que o Compose pinta em qualquer tamanho sem perder nitidez. Foi copiado à
// mão do SVG da prancheta KidsSistema (viewBox de 120 × 120), caminho por
// caminho, sem conversor. Só a pose normal; as outras vêm com a frente F.

package com.velosobr.piuds.components.kids

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** As cores do Pipo (prancheta KidsSistema e README das direções). */
private val PipoYellow = Color(0xFFFFC93C)
private val PipoOrange = Color(0xFFFF8A3D)
private val PipoCoral = Color(0xFFFF6B4A)
private val PipoNavy = Color(0xFF1D2B3A)
private val PipoCheek = Color(0xFFFF9DB5)
private val PipoShadow = Color(0xFFF2E3C6)

/**
 * Um círculo (ou elipse) como caminho: dois arcos de meia volta, de um lado
 * ao outro e de volta. arcTo desenha um pedaço de elipse até o ponto dado.
 */
private fun PathBuilder.ellipse(
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float = rx,
) {
    moveTo(cx - rx, cy)
    arcTo(rx, ry, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = cx + rx, y1 = cy)
    arcTo(rx, ry, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = cx - rx, y1 = cy)
    close()
}

/**
 * O Pipo na pose normal. `by lazy` monta o desenho só na primeira vez que
 * alguém o usa e guarda o resultado. Quando ele é decoração ao lado de um
 * texto, quem o desenha passa contentDescription = null.
 */
val PiuPipo: ImageVector by lazy {
    ImageVector
        .Builder(name = "Pipo", defaultWidth = 120.dp, defaultHeight = 120.dp, viewportWidth = 120f, viewportHeight = 120f)
        .apply {
            // A sombra no chão.
            path(fill = SolidColor(PipoShadow)) { ellipse(60f, 112f, 34f, 6f) }
            // O corpo.
            path(fill = SolidColor(PipoYellow)) {
                moveTo(22f, 70f)
                curveTo(22f, 42f, 40f, 24f, 64f, 24f)
                curveTo(88f, 24f, 100f, 44f, 100f, 64f)
                curveTo(100f, 88f, 82f, 104f, 58f, 104f)
                curveTo(38f, 104f, 22f, 92f, 22f, 70f)
                close()
            }
            // A barriga e a asa, em laranja.
            path(fill = SolidColor(PipoOrange)) {
                moveTo(30f, 74f)
                curveTo(38f, 92f, 62f, 98f, 78f, 90f)
                curveTo(66f, 88f, 48f, 82f, 30f, 74f)
                close()
            }
            path(fill = SolidColor(PipoOrange)) {
                moveTo(40f, 60f)
                curveTo(30f, 64f, 26f, 76f, 34f, 84f)
                curveTo(42f, 80f, 48f, 70f, 46f, 60f)
                close()
            }
            // O olho: o branco, a pupila e o brilho.
            path(fill = SolidColor(Color.White)) { ellipse(74f, 50f, 12f) }
            path(fill = SolidColor(PipoNavy)) { ellipse(77f, 51f, 6f) }
            path(fill = SolidColor(Color.White)) { ellipse(79f, 48f, 2f) }
            // O bico.
            path(fill = SolidColor(PipoCoral)) {
                moveTo(96f, 56f)
                lineTo(114f, 62f)
                lineTo(96f, 68f)
                close()
            }
            // A bochecha, meio transparente.
            path(fill = SolidColor(PipoCheek), fillAlpha = 0.7f) { ellipse(84f, 70f, 5f) }
            // As perninhas: só traço, com a ponta arredondada.
            path(stroke = SolidColor(PipoCoral), strokeLineWidth = 4f, strokeLineCap = StrokeCap.Round) {
                moveTo(50f, 104f)
                lineTo(46f, 114f)
                moveTo(64f, 104f)
                lineTo(66f, 114f)
            }
        }.build()
}

/** Prévia do Pipo no tamanho da prancheta. */
@Preview
@Composable
private fun PiuPipoPreview() {
    Image(PiuPipo, contentDescription = null)
}
