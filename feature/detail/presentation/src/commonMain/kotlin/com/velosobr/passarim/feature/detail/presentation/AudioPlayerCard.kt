// O cartão do player do canto: botão de tocar, barra de progresso, tempos e
// crédito.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.presentation.resources.Res
import com.velosobr.passarim.feature.detail.presentation.resources.audio_error
import com.velosobr.passarim.feature.detail.presentation.resources.audio_pause
import com.velosobr.passarim.feature.detail.presentation.resources.audio_play
import com.velosobr.passarim.feature.detail.presentation.resources.audio_progress
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.resources.ic_pause
import com.velosobr.piuds.resources.ic_play
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.piuds.resources.Res as DesignRes

/**
 * Mostra o canto e deixa tocar, pausar e arrastar a posição. Enquanto o
 * dedo arrasta, a barra segue o dedo (dragging) e não o player; ao soltar, o
 * player pula para a posição escolhida. "by remember { mutableStateOf(...) }"
 * cria um estado que sobrevive às recomposições e, ao mudar, recompõe quem o lê.
 */
@Composable
fun AudioPlayerCard(
    durationMs: Long,
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
    var dragging by remember { mutableStateOf<Float?>(null) }
    val duration = durationMs.coerceAtLeast(1)
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayButton(playback, onTogglePlay)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            val progressLabel = stringResource(Res.string.audio_progress)
            Slider(
                value = dragging ?: (position.toFloat() / duration).coerceIn(0f, 1f),
                onValueChange = { dragging = it },
                onValueChangeFinished = {
                    dragging?.let { onSeek((it * duration).toLong()) }
                    dragging = null
                },
                modifier = Modifier.semantics { contentDescription = progressLabel },
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(position), style = MaterialTheme.typography.labelSmall)
                Text(formatTime(durationMs), style = MaterialTheme.typography.labelSmall)
            }
            if (playback == PlaybackState.Error) {
                Text(
                    stringResource(Res.string.audio_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            PiuCreditCaption(credit)
        }
    }
}

/** O botão redondo: spinner enquanto carrega, pausa enquanto toca, play no resto. */
@Composable
private fun PlayButton(
    playback: PlaybackState,
    onTogglePlay: () -> Unit,
) {
    val playing = playback is PlaybackState.Playing || playback == PlaybackState.Loading
    val label = stringResource(if (playing) Res.string.audio_pause else Res.string.audio_play)
    FilledIconButton(onClick = onTogglePlay, modifier = Modifier.size(56.dp).semantics { contentDescription = label }) {
        if (playback == PlaybackState.Loading) {
            CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
        } else {
            Icon(
                vectorResource(if (playing) DesignRes.drawable.ic_pause else DesignRes.drawable.ic_play),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
