// "Reduzir animações": a preferência de acessibilidade do sistema, lida em
// cada plataforma. "expect" declara aqui o que cada plataforma entrega com
// "actual" (Android em androidMain, iOS em iosMain).

package com.velosobr.piuds.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Fixa "reduzir animações" de fora, sem ler o sistema: os testes e os
 * snapshots usam true ou false. null (o padrão) = ler a preferência do sistema.
 */
val LocalPiuReduceMotion = staticCompositionLocalOf<Boolean?> { null }

/** Devolve uma função que lê a preferência do sistema agora (cada plataforma sabe onde ela fica). */
@Composable
internal expect fun systemReduceMotionReader(): () -> Boolean

/**
 * "Reduzir animações" resolvido: o valor fixado de fora ([override]) ou a
 * preferência do sistema, relida a cada volta ao app (ON_RESUME). A pessoa
 * pode ligar "remover animações" nos ajustes e voltar, e o app precisa
 * perceber sem reiniciar. Com [override], o sistema nem é lido e o efeito de
 * ciclo de vida não é registrado (os snapshots não têm ciclo de vida real).
 */
@Composable
internal fun rememberReduceMotion(override: Boolean?): Boolean {
    val read = systemReduceMotionReader()
    var value by remember(read, override) { mutableStateOf(resolveReduceMotion(override, read)) }
    if (override == null) {
        LifecycleResumeEffect(read) {
            value = read()
            onPauseOrDispose { }
        }
    }
    return value
}
