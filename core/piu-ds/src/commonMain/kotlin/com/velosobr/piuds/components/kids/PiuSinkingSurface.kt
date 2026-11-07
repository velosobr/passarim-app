// A base dos botões do Kids: uma face colorida apoiada numa "sombra sólida"
// (uma faixa da mesma forma, mais escura, logo abaixo). Ao tocar, a face
// desce até cobrir a sombra, como um botão de brinquedo que afunda, e volta
// ao soltar. Usada pelo PiuKidsButton e pelo PiuWorldCard.

package com.velosobr.piuds.components.kids

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.theme.PiuMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A superfície que afunda. A altura total é [minHeight] mais a sombra: a
 * sombra fica fixa embaixo, e a face desce [shadowOffset] quando pressionada.
 *
 * MutableInteractionSource é o "canal" por onde o clickable conta o que o
 * dedo está fazendo: um Press quando ele encosta, um Release quando solta (ou
 * um Cancel, se o gesto vira rolagem). O deslocamento é um Animatable, um
 * valor animado que a gente comanda por código, com a mola de toque do
 * PiuMotion (seca, com "reduzir animações"). Quem chama pode passar o próprio
 * [interactionSource] para acompanhar o toque.
 *
 * Por que não só "está pressionado ou não": dentro de uma lista rolável, o
 * clickable espera um instante para saber se o dedo vai rolar, e num toque
 * rápido o Press e o Release chegam juntos. Olhando só o estado, a face nunca
 * desceria. Aqui, cada Press garante uma descida inteira, e a volta só começa
 * depois dela.
 */
@Composable
internal fun PiuSinkingSurface(
    face: Brush,
    shadowColor: Color,
    shadowOffset: Dp,
    shape: Shape,
    minHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    // Dp.VectorConverter ensina o Animatable a animar um Dp (ele anima números por dentro).
    val sink = remember { Animatable(0.dp, Dp.VectorConverter) }
    // rememberUpdatedState: o efeito abaixo roda uma vez só, mas sempre lê a mola atual (ela muda
    // quando a pessoa liga ou desliga "reduzir animações").
    val spring by rememberUpdatedState(PiuMotion.press<Dp>())
    val depth by rememberUpdatedState(shadowOffset)
    LaunchedEffect(interaction) {
        var descent: Job? = null
        interaction.interactions.collect { event ->
            when (event) {
                is PressInteraction.Press -> descent = launch { sink.animateTo(depth, spring) }
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    val pending = descent
                    launch {
                        pending?.join()
                        sink.animateTo(0.dp, spring)
                    }
                }
            }
        }
    }
    Box(
        modifier
            .alpha(if (enabled) 1f else 0.5f)
            // indication = null: o retorno visual do toque é o afundar, não a "onda" padrão do Material.
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick),
        // propagateMinConstraints: a largura mínima que vem de fora (um cartão esticado na grade, por
        // exemplo) chega à face, que então ocupa o cartão inteiro em vez de só o tamanho do texto.
        propagateMinConstraints = true,
    ) {
        Box(Modifier.matchParentSize().padding(top = shadowOffset).background(shadowColor, shape))
        Box(
            Modifier
                .padding(bottom = shadowOffset)
                // offset { } desloca a face sem mudar o tamanho de ninguém. A mola de toque passa um
                // pouco do fim e volta (o balanço); com padding, a passada daria um espaço negativo e
                // derrubaria o app. E o estado é lido só na fase de layout: cada quadro da animação
                // reposiciona a face sem recompor o botão.
                .offset { IntOffset(0, sink.value.roundToPx()) }
                .defaultMinSize(minHeight = minHeight)
                .background(face, shape),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}
