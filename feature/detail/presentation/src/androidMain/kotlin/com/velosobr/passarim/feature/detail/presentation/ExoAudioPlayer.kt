// O player de áudio do Android, sobre o ExoPlayer (Media3).

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

/** De quanto em quanto tempo a posição do canto é publicada enquanto toca. */
private const val PROGRESS_INTERVAL_MILLIS = 200L

/**
 * Implementa o AudioPlayer com o ExoPlayer, criado só no primeiro play. O
 * estado é publicado num StateFlow a cada mudança do player e, enquanto toca,
 * a cada 200 ms (para a barra de progresso andar). MainScope é um escopo de
 * coroutines na thread principal, onde o ExoPlayer exige ser usado.
 */
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

    // O ouvinte do ExoPlayer: ao terminar, volta ao começo pausado; ao começar ou
    // parar de tocar, liga ou desliga a atualização da posição; num erro, marca
    // a falha.
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

    /** Toca a URL; só recarrega a mídia se a URL mudou ou se a anterior falhou. */
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

    /** Pausa e publica o estado. */
    override fun pause() {
        player?.pause()
        publish()
    }

    /** Pula para a posição e publica o estado. */
    override fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
        publish()
    }

    /** Para tudo e libera o ExoPlayer (que segura recursos do sistema). */
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

    /**
     * Cria o ExoPlayer como player de mídia. O "true" de setAudioAttributes faz
     * o ExoPlayer cuidar do foco de áudio: pausa se outro app começar a tocar.
     */
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

    /** Publica a posição em intervalos enquanto toca. */
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

    /** Traduz o estado do ExoPlayer no PlaybackState do app. */
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
