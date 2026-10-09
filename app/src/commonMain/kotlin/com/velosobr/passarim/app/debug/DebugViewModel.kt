package com.velosobr.passarim.app.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.onFailure
import com.velosobr.passarim.core.domain.onSuccess
import com.velosobr.passarim.core.presentation.UiText
import com.velosobr.passarim.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DebugState(
    val isLoading: Boolean = true,
    val biomeCount: Int? = null,
    val stateCount: Int? = null,
    val error: UiText? = null,
)

sealed interface DebugAction {
    data object Retry : DebugAction
}

/**
 * Exemplo mínimo de MVI na 5a: busca `GET /v1/filters` e expõe as contagens. Some quando a 5b
 * entrega o `FiltersRepository` de verdade na tela Explorar.
 */
class DebugViewModel(
    private val filters: FiltersRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DebugState())
    val state: StateFlow<DebugState> = _state.asStateFlow()

    init {
        load()
    }

    fun onAction(action: DebugAction) {
        when (action) {
            DebugAction.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            filters
                .get()
                .onSuccess { f ->
                    _state.update { DebugState(isLoading = false, biomeCount = f.biomes.size, stateCount = f.states.size) }
                }.onFailure { e ->
                    _state.update { DebugState(isLoading = false, error = e.toUiText()) }
                }
        }
    }
}
