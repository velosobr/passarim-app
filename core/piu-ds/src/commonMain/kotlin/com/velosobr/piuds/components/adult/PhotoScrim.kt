// O scrim das fotos do adulto: o degradê escuro atrás do nome da ave, para o
// texto branco ter contraste sobre qualquer foto. Usado pelo card do mosaico
// e pela "Ave do dia"; fica interno ao PiuDS.

package com.velosobr.piuds.components.adult

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pinta o scrim atrás do bloco de texto a que é aplicado: transparente no
 * topo do bloco, chega à cor cheia do scrim ([color], a dos tokens) em
 * [fade] e fica cheia daí para baixo.
 *
 * O scrim acompanha o texto, e não a altura do card: com fonte grande o
 * bloco cresce para cima, e o scrim cresce junto. Quem usa deixa no topo do
 * bloco um espaço maior que [fade] (os cards guardam 56 dp para o coração e
 * o selo), então o texto fica sempre sobre a cor cheia, o par que a
 * ferramenta de contraste mede (branco sobre o scrim numa foto branca).
 *
 * drawBehind desenha antes do conteúdo, no tamanho do próprio bloco. O
 * degradê vai de 0 a [fade] em pixels; depois do fim, ele repete a última
 * cor (o padrão "clamp"), por isso o resto do bloco fica todo escuro.
 */
internal fun Modifier.photoTextScrim(
    color: Color,
    fade: Dp = 32.dp,
): Modifier =
    drawBehind {
        drawRect(Brush.verticalGradient(listOf(color.copy(alpha = 0f), color), startY = 0f, endY = fade.toPx()))
    }
