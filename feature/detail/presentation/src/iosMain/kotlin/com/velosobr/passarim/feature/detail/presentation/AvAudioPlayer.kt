// O player de áudio do iOS, sobre o AVPlayer (AVFoundation da Apple).

package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.feature.detail.domain.AudioPlayer
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSObjectProtocol

/** De quanto em quanto tempo a posição é publicada; TIMESCALE 1000 faz o CMTime contar em milésimos de segundo. */
private const val PROGRESS_INTERVAL_MILLIS = 200L
private const val MILLIS_PER_SECOND = 1_000.0
private const val TIMESCALE = 1_000

/**
 * Implementa o AudioPlayer com o AVPlayer. O CMTime e as funções do CoreMedia são
 * structs e funções em C, e os parâmetros `error` são ponteiros; o Kotlin/Native só
 * deixa usar esse tipo de API com @OptIn(ExperimentalForeignApi). O estado é
 * publicado a cada 200 ms enquanto toca ou carrega.
 */
@OptIn(ExperimentalForeignApi::class)
class AvAudioPlayer : AudioPlayer {
    private val _state = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val scope = MainScope()
    private var player: AVPlayer? = null
    private var currentUrl: String? = null
    private var endObserver: NSObjectProtocol? = null
    private var progressJob: Job? = null

    /**
     * Ativa a sessão de áudio e toca; só troca o item se a URL mudou ou se a
     * anterior falhou. Uma URL que o iOS não entende vira erro na hora.
     */
    override fun play(url: String) {
        activateSession()
        val av = player ?: AVPlayer().also { player = it }
        if (url != currentUrl || _state.value == PlaybackState.Error) {
            val nsUrl = NSURL.URLWithString(url)
            if (nsUrl == null) {
                _state.value = PlaybackState.Error
                return
            }
            currentUrl = url
            val item = AVPlayerItem(uRL = nsUrl)
            observeEnd(item)
            av.replaceCurrentItemWithPlayerItem(item)
        }
        av.play()
        _state.value = PlaybackState.Loading
        startProgress()
    }

    /** Pausa e publica o estado. */
    override fun pause() {
        player?.pause()
        publish()
    }

    /** Pula para a posição (CMTime em segundos, na escala de milésimos). */
    override fun seekTo(positionMs: Long) {
        player?.seekToTime(CMTimeMakeWithSeconds(positionMs / MILLIS_PER_SECOND, TIMESCALE))
        publish()
    }

    /** Para tudo, solta o item e remove o observador do fim. */
    override fun release() {
        progressJob?.cancel()
        scope.cancel()
        removeEndObserver()
        player?.run {
            pause()
            replaceCurrentItemWithPlayerItem(null)
        }
        player = null
        currentUrl = null
        _state.value = PlaybackState.Idle
    }

    /** A categoria Playback faz o som tocar mesmo com a chave de silencioso ligada e interrompe o áudio de outros apps. */
    private fun activateSession() {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)
    }

    /** Quando o canto acaba, volta ao começo pausado (o iOS avisa por notificação). */
    private fun observeEnd(item: AVPlayerItem) {
        removeEndObserver()
        endObserver =
            NSNotificationCenter.defaultCenter.addObserverForName(
                name = AVPlayerItemDidPlayToEndTimeNotification,
                `object` = item,
                queue = NSOperationQueue.mainQueue,
            ) { _ ->
                player?.run {
                    pause()
                    seekToTime(CMTimeMakeWithSeconds(0.0, TIMESCALE))
                }
                _state.value = PlaybackState.Paused(0)
            }
    }

    /** Remove o observador do fim, se houver. */
    private fun removeEndObserver() {
        endObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        endObserver = null
    }

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

    /** Traduz o estado do AVPlayer no PlaybackState e para a atualização quando não está tocando. */
    private fun publish() {
        val av = player
        val position = av?.let { (CMTimeGetSeconds(it.currentTime()) * MILLIS_PER_SECOND).toLong().coerceAtLeast(0) } ?: 0
        _state.value =
            when {
                av == null || currentUrl == null -> PlaybackState.Idle
                av.currentItem?.status == AVPlayerItemStatusFailed -> PlaybackState.Error
                av.timeControlStatus == AVPlayerTimeControlStatusPlaying -> PlaybackState.Playing(position)
                av.timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate -> PlaybackState.Loading
                else -> PlaybackState.Paused(position)
            }
        if (_state.value !is PlaybackState.Playing && _state.value != PlaybackState.Loading) progressJob?.cancel()
    }
}
