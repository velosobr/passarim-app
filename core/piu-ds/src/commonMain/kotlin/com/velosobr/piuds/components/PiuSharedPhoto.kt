// A foto que viaja de uma tela para outra (frente E, seção 6): ao abrir o
// Detalhe, a foto do card do mosaico cresce até o topo do Detalhe, e volta ao
// card no "voltar". É um "elemento compartilhado" do Compose: duas peças, uma
// em cada tela, com a mesma chave; durante a troca de tela, o Compose anima
// os limites de uma até a outra.
//
// O app liga as peças: PiuSharedTransitionLayout em volta da navegação e
// PiuSharedScope em cada tela; os componentes só marcam a foto com
// piuSharedPhoto e uma chave. Com "reduzir animações" ligado, ou fora dessa
// estrutura (prévias e snapshots), nada viaja e a troca de tela é a comum.
// Os dois CompositionLocals e as três funções ficam juntos neste arquivo, por
// isso o detekt não cobra um arquivo com o nome de cada declaração.
@file:Suppress("MatchingDeclarationName")
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.velosobr.piuds.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.velosobr.piuds.theme.PiuTheme

/**
 * A camada que desenha a foto enquanto ela viaja. compositionLocalOf é um valor que desce pela
 * árvore de composição sem passar de parâmetro em parâmetro: quem está dentro de
 * PiuSharedTransitionLayout o lê; fora dela, é nulo.
 */
private val LocalPiuSharedTransition = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * O escopo de animação da tela atual (entrando ou saindo). É ele que diz ao Compose em que ponto
 * da troca de tela a foto está.
 */
private val LocalPiuSharedVisibility = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Envolve a navegação (o NavHost da raiz) para as fotos poderem viajar entre as telas dela.
 * SharedTransitionLayout é a camada do Compose que acompanha os elementos compartilhados e os
 * desenha por cima de tudo durante a troca.
 */
@Composable
fun PiuSharedTransitionLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SharedTransitionLayout(modifier) {
        CompositionLocalProvider(LocalPiuSharedTransition provides this) { content() }
    }
}

/**
 * Entrega às fotos de uma tela o [scope] de animação dela: no NavHost, o `this` de cada
 * `composable<Rota> { }`. As telas de dentro de outro NavHost (as abas) herdam o escopo da tela
 * da raiz que as contém, e é isso que deixa a foto atravessar dos dois NavHosts.
 */
@Composable
fun PiuSharedScope(
    scope: AnimatedVisibilityScope,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalPiuSharedVisibility provides scope) { content() }
}

/**
 * Marca a foto com a [key] que a liga à foto da outra tela ("foto-<id>"). Sem chave, sem a camada,
 * sem escopo ou com "reduzir animações", devolve o próprio modificador, sem nada a mais.
 *
 * Usa sharedBounds, e não sharedElement: o card mostra a miniatura e o Detalhe a foto grande
 * (que ainda está baixando). sharedBounds faz a ponte entre conteúdos diferentes: os limites
 * viajam enquanto um lado some e o outro aparece. [shape] recorta a foto durante a viagem (os
 * cantos arredondados do card).
 */
@Composable
fun Modifier.piuSharedPhoto(
    key: String?,
    shape: Shape = RectangleShape,
): Modifier {
    val transition = LocalPiuSharedTransition.current
    val visibility = LocalPiuSharedVisibility.current
    val outsideTransition = transition == null || visibility == null
    if (key == null || outsideTransition || PiuTheme.reduceMotion) return this
    return with(transition) {
        this@piuSharedPhoto.sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = visibility,
            clipInOverlayDuringTransition = OverlayClip(shape),
        )
    }
}
