package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
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

@Composable
fun AboutRoot(
    onBack: () -> Unit,
    onOpenLicenses: () -> Unit,
    viewModel: AboutViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AboutEvent.NavigateBack -> onBack()
            is AboutEvent.OpenUrl -> uriHandler.openUri(event.url)
            AboutEvent.NavigateToLicenses -> onOpenLicenses()
        }
    }
    AboutScreen(state = state, onAction = viewModel::onAction)
}
