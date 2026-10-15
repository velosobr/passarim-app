package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoot(
    onOpenStorage: () -> Unit,
    onOpenAbout: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.NavigateToStorage -> onOpenStorage()
            SettingsEvent.NavigateToAbout -> onOpenAbout()
        }
    }
    SettingsScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun StorageRoot(
    onBack: () -> Unit,
    viewModel: StorageViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            StorageEvent.NavigateBack -> onBack()
        }
    }
    StorageScreen(state = state, onAction = viewModel::onAction)
}
