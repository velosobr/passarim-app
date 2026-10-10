package com.velosobr.passarim.app.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.velosobr.passarim.app.resources.Res
import com.velosobr.passarim.app.resources.debug_biomes
import com.velosobr.passarim.app.resources.debug_retry
import com.velosobr.passarim.app.resources.debug_states
import com.velosobr.passarim.app.resources.debug_title
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DebugRoot(viewModel: DebugViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    DebugScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun DebugScreen(
    state: DebugState,
    onAction: (DebugAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(PassarimDimens.screenMargin),
        verticalArrangement = Arrangement.spacedBy(PassarimDimens.screenMargin),
    ) {
        Text(stringResource(Res.string.debug_title), style = MaterialTheme.typography.titleLarge)
        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> {
                Text(state.error.asString(), color = MaterialTheme.colorScheme.error)
                Button(onClick = { onAction(DebugAction.Retry) }) { Text(stringResource(Res.string.debug_retry)) }
            }
            else -> {
                Text(stringResource(Res.string.debug_biomes, state.biomeCount ?: 0))
                Text(stringResource(Res.string.debug_states, state.stateCount ?: 0))
            }
        }
    }
}
