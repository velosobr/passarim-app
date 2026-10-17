// O contrato do player de áudio e os estados da reprodução. A implementação
// é de cada plataforma: ExoAudioPlayer no Android e AvAudioPlayer no iOS.

package com.velosobr.passarim.feature.detail.domain

import kotlinx.coroutines.flow.StateFlow

/** Em que ponto está o canto: parado, carregando, tocando, pausado ou com erro. */
sealed interface PlaybackState {
    data object Idle : PlaybackState

    data object Loading : PlaybackState

    data class Playing(
        val positionMs: Long,
    ) : PlaybackState

    data class Paused(
        val positionMs: Long,
    ) : PlaybackState

    data object Error : PlaybackState
}

/**
 * Toca um canto por URL. [state] é observado pela tela; [play] com outra URL
 * troca o canto; [release] libera o player quando a tela sai de vez.
 */
interface AudioPlayer {
    val state: StateFlow<PlaybackState>

    fun play(url: String)

    fun pause()

    fun seekTo(positionMs: Long)

    fun release()
}
