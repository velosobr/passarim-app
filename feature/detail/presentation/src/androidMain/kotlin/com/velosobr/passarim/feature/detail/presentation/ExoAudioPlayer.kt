package com.velosobr.passarim.feature.detail.presentation

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.velosobr.passarim.feature.detail.domain.AudioPlayer
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val PROGRESS_INTERVAL_MILLIS = 200L

class ExoAudioPlayer(
    private val context: Context,
) : AudioPlayer {
    private val _state = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val scope = MainScope()
    private var player: ExoPlayer? = null
    private var currentUrl: String? = null
    private var failed = false
    private var progressJob: Job? = null

    private val listener =
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    player?.run {
                        pause()
                        seekTo(0)
                    }
                }
                publish()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) startProgress() else progressJob?.cancel()
                publish()
            }

            override fun onPlayerError(error: PlaybackException) {
                failed = true
                publish()
            }
        }

    override fun play(url: String) {
        val exo = player ?: createPlayer().also { player = it }
        if (url != currentUrl || failed) {
            currentUrl = url
            failed = false
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
        }
        exo.play()
        publish()
    }

    override fun pause() {
        player?.pause()
        publish()
    }

    override fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
        publish()
    }

    override fun release() {
        progressJob?.cancel()
        scope.cancel()
        player?.run {
            removeListener(listener)
            release()
        }
        player = null
        currentUrl = null
        _state.value = PlaybackState.Idle
    }

    private fun createPlayer(): ExoPlayer =
        ExoPlayer
            .Builder(context)
            .setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            ).build()
            .also { it.addListener(listener) }

    private fun startProgress() {
        progressJob?.cancel()
        progressJob =
            scope.launch {
                while (isActive) {
                    publish()
                    delay(PROGRESS_INTERVAL_MILLIS)
                }
            }
    }

    private fun publish() {
        val exo = player
        _state.value =
            when {
                exo == null || currentUrl == null -> PlaybackState.Idle
                failed -> PlaybackState.Error
                exo.isPlaying -> PlaybackState.Playing(exo.currentPosition)
                exo.playWhenReady && exo.playbackState == Player.STATE_BUFFERING -> PlaybackState.Loading
                else -> PlaybackState.Paused(exo.currentPosition)
            }
    }
}
