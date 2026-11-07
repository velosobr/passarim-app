// A base dos botões do Kids: uma face colorida apoiada numa "sombra sólida"
// (uma faixa da mesma forma, mais escura, logo abaixo). Ao tocar, a face
// desce até cobrir a sombra, como um botão de brinquedo que afunda, e volta
// ao soltar. Usada pelo PiuKidsButton e pelo PiuWorldCard.

package com.velosobr.piuds.components.kids

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

/**
 * A superfície que afunda. A altura total é [minHeight] mais a sombra: a
 * sombra fica fixa embaixo, e a face desce [shadowOffset] quando pressionada.
 *
 * MutableInteractionSource é o "canal" por onde o clickable conta o que o
 * dedo está fazendo; collectIsPressedAsState transforma isso num Boolean que
 * redesenha a tela quando muda. animateDpAsState anima o deslocamento com a
 * mola de toque do PiuMotion (seca, com "reduzir animações"). Quem chama pode
 * passar o próprio [interactionSource] para acompanhar o toque (ou, num
 * snapshot, simular o dedo segurando).
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
    val pressed by interaction.collectIsPressedAsState()
    val sink by animateDpAsState(if (pressed && enabled) shadowOffset else 0.dp, PiuMotion.press(), label = "afundar")
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
                .offset { IntOffset(0, sink.roundToPx()) }
                .defaultMinSize(minHeight = minHeight)
                .background(face, shape),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}
