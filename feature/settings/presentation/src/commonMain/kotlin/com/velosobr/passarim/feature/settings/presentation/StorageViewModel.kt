// O ViewModel de Dados e armazenamento.

package com.velosobr.passarim.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.ImageCacheCleaner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** Limpa o cache uma vez por toque (um segundo toque durante a limpeza é ignorado). */
class StorageViewModel(
    private val cleaner: ImageCacheCleaner,
) : ViewModel() {
    private val _state = MutableStateFlow<StorageState>(StorageState.Idle)
    val state: StateFlow<StorageState> = _state.asStateFlow()

    private val eventChannel = Channel<StorageEvent>(Channel.BUFFERED)
    val events: Flow<StorageEvent> = eventChannel.receiveAsFlow()

    /** Trata cada ação da tela. */
    fun onAction(action: StorageAction) {
        when (action) {
            StorageAction.ClearCache -> clearCache()
            StorageAction.Back -> eventChannel.trySend(StorageEvent.NavigateBack)
        }
    }

    /** Limpa o cache e avisa quando terminar; ignora o toque se já estiver limpando. */
    private fun clearCache() {
        if (_state.value == StorageState.Clearing) return
        _state.value = StorageState.Clearing
        viewModelScope.launch {
            cleaner.clearImageCache()
            _state.value = StorageState.Cleared
        }
    }
}
