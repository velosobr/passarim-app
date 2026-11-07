// Superfície com desfoque do que está atrás (o "vidro fosco" das barras sobre
// a foto). É o ÚNICO arquivo que importa o Haze, a biblioteca de desfoque:
// se ela quebrar numa versão nova, troca-se só este arquivo por um fundo
// translúcido sem desfoque, e nenhuma tela muda.

package com.velosobr.piuds.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource

/**
 * O "estado" do desfoque: liga quem é desfocado ([piuBlurSource]) a quem
 * desfoca ([PiuBlurSurface]). Crie um por tela com [rememberPiuBlurState].
 */
class PiuBlurState internal constructor(
    internal val haze: HazeState,
)

/** Cria e guarda o estado do desfoque entre recomposições. */
@Composable
fun rememberPiuBlurState(): PiuBlurState = remember { PiuBlurState(HazeState()) }

/** Marca o conteúdo (a foto, a lista) que as superfícies de [state] desfocam. */
fun Modifier.piuBlurSource(state: PiuBlurState): Modifier = hazeSource(state.haze)

/**
 * A superfície: desfoca o conteúdo marcado com [piuBlurSource] que está
 * atrás dela e pinta por cima a [tint] translúcida. A tinta sozinha já dá
 * contraste ao que vai em cima: o desfoque é só decoração, e por isso a
 * superfície continua legível onde o desfoque não roda.
 */
@Composable
fun PiuBlurSurface(
    state: PiuBlurState,
    shape: Shape,
    tint: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .clip(shape)
            .hazeBlur(HazeInput.Sources(state.haze), HazeBlurStyle { blurRadius(24.dp) })
            .background(tint),
        content = content,
    )
}
