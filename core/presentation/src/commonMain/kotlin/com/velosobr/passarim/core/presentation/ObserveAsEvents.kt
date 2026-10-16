// Coleta de eventos de uma vez (navegar, mostrar aviso) que o ViewModel manda.

package com.velosobr.passarim.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Coleta eventos pontuais (navegar, mostrar mensagem) só enquanto a tela está visível,
 * sem perder nem repetir eventos ao girar a tela.
 *
 * LaunchedEffect roda um bloco suspend enquanto a função está na tela e o
 * reinicia quando uma das chaves muda. repeatOnLifecycle(STARTED) só coleta
 * com a tela visível e para quando ela vai para segundo plano. O
 * Dispatchers.Main.immediate entrega o evento na hora, na thread da tela,
 * sem esperar a próxima volta do laço, o que evita perder um evento numa
 * rotação.
 */
@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, events) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect(onEvent)
            }
        }
    }
}
