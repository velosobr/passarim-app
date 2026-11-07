// O scrim das fotos do adulto: o degradê escuro que cobre a parte de baixo
// da foto para o texto branco por cima ter contraste. Usado pelo card do
// mosaico e pela "Ave do dia"; fica interno ao PiuDS.

package com.velosobr.piuds.components.adult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.velosobr.piuds.theme.PiuTheme

/**
 * O scrim: degradê de transparente (até [startFraction] da altura) até o
 * scrim dos tokens, embaixo. Ele dá ao texto branco contraste sobre qualquer
 * foto (a ferramenta de contraste mede o pior caso, foto branca, na faixa
 * mais escura). colorStops dá a posição de cada cor, de 0 (topo) a 1 (base).
 */
@Composable
internal fun PhotoScrim(
    modifier: Modifier,
    startFraction: Float = PiuTheme.tokens.scrimStart,
) {
    val scrim = PiuTheme.tokens.scrim
    Box(modifier.background(Brush.verticalGradient(0f to scrim.copy(alpha = 0f), startFraction to scrim.copy(alpha = 0f), 1f to scrim)))
}
