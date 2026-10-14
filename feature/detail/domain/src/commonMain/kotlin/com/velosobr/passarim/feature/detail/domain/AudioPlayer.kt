package com.velosobr.passarim.feature.detail.domain

import kotlinx.coroutines.flow.StateFlow

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

interface AudioPlayer {
    val state: StateFlow<PlaybackState>

    fun play(url: String)

    fun pause()

    fun seekTo(positionMs: Long)

    fun release()
}
