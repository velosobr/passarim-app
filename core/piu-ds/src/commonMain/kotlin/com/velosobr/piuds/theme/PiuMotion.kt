// As molas do PiuDS: o lugar único de onde os componentes tiram as animações.
// Uma "mola" (spring) anima como um elástico: o valor chega ao destino com
// um pequeno balanço, sem duração fixa. Com "reduzir animações" ligado, toda
// mola vira uma troca seca (snap), e o componente vai direto ao estado final.
// Quando o MotionScheme expressivo do Material 3 sair em versão estável, é
// este arquivo que ele substitui (TODO(frente-H) no PiuTheme).

package com.velosobr.piuds.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * As duas molas nomeadas do PiuDS. Os componentes nunca escrevem `spring()`
 * direto: assim "reduzir animações" vale para todos de uma vez.
 */
object PiuMotion {
    /**
     * Para resposta ao toque (o botão que afunda e volta): rápida e com um
     * pequeno balanço, a "sensação de brinquedo".
     */
    @Composable
    @ReadOnlyComposable
    fun <T> press(): FiniteAnimationSpec<T> =
        if (PiuTheme.reduceMotion) snap() else spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)

    /** Para troca de estado (o item da barra que expande): sem balanço. */
    @Composable
    @ReadOnlyComposable
    fun <T> state(): FiniteAnimationSpec<T> =
        if (PiuTheme.reduceMotion) snap() else spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow)
}
