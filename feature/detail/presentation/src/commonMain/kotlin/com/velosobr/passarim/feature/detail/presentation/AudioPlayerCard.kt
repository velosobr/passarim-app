// O cartão do player do canto (prancheta Detalhe.dc.html): o botão de tocar
// em forma de cookie, a forma de onda com a parte tocada na cor primária, os
// tempos e o crédito da gravação.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.presentation.resources.Res
import com.velosobr.passarim.feature.detail.presentation.resources.audio_error
import com.velosobr.passarim.feature.detail.presentation.resources.audio_pause
import com.velosobr.passarim.feature.detail.presentation.resources.audio_play
import com.velosobr.passarim.feature.detail.presentation.resources.audio_progress
import com.velosobr.piuds.components.PiuCookieShape
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.resources.ic_pause
import com.velosobr.piuds.resources.ic_play
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.piuds.resources.Res as DesignRes

/**
 * Mostra o canto e deixa tocar, pausar e pular a posição tocando ou arrastando na forma de onda.
 * [peaks] são as alturas da forma de onda (vazia vira uma linha reta). Enquanto o dedo arrasta, a
 * forma de onda segue o dedo (dragging) e não o player; ao soltar, o player pula para a posição
 * escolhida. "by remember { mutableStateOf(...) }" cria um estado que sobrevive às recomposições
 * e, ao mudar, recompõe quem o lê.
 *
 * O fundo é um degradê suave da primaryContainer para a surfaceContainer, como na prancheta; as
 * duas cores vêm do tema, então o par funciona no claro e no escuro.
 */
@Composable
fun AudioPlayerCard(
    durationMs: Long,
    peaks: List<Float>,
    credit: String,
    playback: PlaybackState,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val position =
        when (playback) {
            is PlaybackState.Playing -> playback.positionMs
            is PlaybackState.Paused -> playback.positionMs
            else -> 0L
        }
    val duration = durationMs.coerceAtLeast(1)
    var dragging by remember { mutableStateOf<Float?>(null) }
    val colors = MaterialTheme.colorScheme
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(colors.primaryContainer.copy(alpha = 0.55f), colors.surfaceContainer)))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayButton(playback, onTogglePlay)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Waveform(
                peaks = peaks,
                progress = dragging ?: (position.toFloat() / duration).coerceIn(0f, 1f),
                modifier =
                    Modifier.seekable(
                        label = stringResource(Res.string.audio_progress),
                        progress = (position.toFloat() / duration).coerceIn(0f, 1f),
                        onDrag = { dragging = it },
                        onSeek = { fraction ->
                            dragging = null
                            onSeek((fraction * duration).toLong())
                        },
                    ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(position), style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
                Text(formatTime(durationMs), style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
            if (playback == PlaybackState.Error) {
                Text(
                    stringResource(Res.string.audio_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.error,
                )
            }
            PiuCreditCaption(credit)
        }
    }
}

/**
 * Deixa pular a posição do canto pela forma de onda, de três jeitos: tocando num ponto, arrastando
 * (a forma de onda acompanha o dedo por [onDrag] e só pula ao soltar) e pelo leitor de tela.
 * pointerInput recebe os toques crus; a fração é a posição do dedo dividida pela largura.
 * Na semântica, progressBarRangeInfo diz ao leitor de tela quanto já tocou ("50%"), e setProgress
 * é a ação que ele usa para mudar a posição (o gesto de deslizar para cima ou para baixo no
 * TalkBack). rememberUpdatedState deixa os gestos, criados uma vez, chamarem sempre o onSeek atual.
 */
@Composable
private fun Modifier.seekable(
    label: String,
    progress: Float,
    onDrag: (Float) -> Unit,
    onSeek: (Float) -> Unit,
): Modifier {
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnSeek by rememberUpdatedState(onSeek)
    var lastDrag by remember { mutableStateOf(0f) }
    return this
        .pointerInput(Unit) {
            detectTapGestures { offset -> currentOnSeek((offset.x / size.width).coerceIn(0f, 1f)) }
        }.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = { currentOnSeek(lastDrag) },
                onHorizontalDrag = { change, _ ->
                    lastDrag = (change.position.x / size.width).coerceIn(0f, 1f)
                    currentOnDrag(lastDrag)
                },
            )
        }.semantics {
            contentDescription = label
            progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
            setProgress { target ->
                currentOnSeek(target.coerceIn(0f, 1f))
                true
            }
        }
}

/**
 * O botão de tocar: cookie de 64 dp (a forma de assinatura do PiuDS) na cor primária, com spinner
 * enquanto carrega, pausa enquanto toca e play no resto. Role.Button faz o leitor de tela dizer
 * "botão" depois da descrição.
 */
@Composable
private fun PlayButton(
    playback: PlaybackState,
    onTogglePlay: () -> Unit,
) {
    val playing = playback is PlaybackState.Playing || playback == PlaybackState.Loading
    val label = stringResource(if (playing) Res.string.audio_pause else Res.string.audio_play)
    Box(
        Modifier
            .size(64.dp)
            .clip(PiuCookieShape())
            .background(MaterialTheme.colorScheme.primary)
            .clickable(role = Role.Button, onClick = onTogglePlay)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        if (playback == PlaybackState.Loading) {
            CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
        } else {
            Icon(
                vectorResource(if (playing) DesignRes.drawable.ic_pause else DesignRes.drawable.ic_play),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
