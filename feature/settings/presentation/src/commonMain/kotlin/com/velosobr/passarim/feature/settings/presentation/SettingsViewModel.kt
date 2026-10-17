// O ViewModel da aba Configurações.

package com.velosobr.passarim.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Observa as preferências e grava cada mudança no repositório. */
class SettingsViewModel(
    private val repository: SettingsRepository,
    dynamicColorsSupported: Boolean,
    versionName: String,
) : ViewModel() {
    private val initial = SettingsState(dynamicColorsSupported = dynamicColorsSupported, versionName = versionName)

    val state: StateFlow<SettingsState> =
        repository
            .observe()
            .map { initial.copy(settings = it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, initial)

    private val eventChannel = Channel<SettingsEvent>(Channel.BUFFERED)
    val events: Flow<SettingsEvent> = eventChannel.receiveAsFlow()

    /** Trata cada ação da tela. */
    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SelectTheme -> viewModelScope.launch { repository.setTheme(action.theme) }
            is SettingsAction.ChangeDynamicColors -> viewModelScope.launch { repository.setDynamicColors(action.enabled) }
            is SettingsAction.ChangeAutoPlay -> viewModelScope.launch { repository.setAutoPlay(action.enabled) }
            SettingsAction.OpenStorage -> eventChannel.trySend(SettingsEvent.NavigateToStorage)
            SettingsAction.OpenAbout -> eventChannel.trySend(SettingsEvent.NavigateToAbout)
        }
    }
}
