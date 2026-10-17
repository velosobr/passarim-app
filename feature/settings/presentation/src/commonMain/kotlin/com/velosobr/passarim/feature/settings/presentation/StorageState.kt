// O estado, as ações e os eventos de Dados e armazenamento.

package com.velosobr.passarim.feature.settings.presentation

/** Parado, limpando ou limpo. */
sealed interface StorageState {
    data object Idle : StorageState

    data object Clearing : StorageState

    data object Cleared : StorageState
}

/** Limpar o cache ou voltar. */
sealed interface StorageAction {
    data object ClearCache : StorageAction

    data object Back : StorageAction
}

/** Voltar. */
sealed interface StorageEvent {
    data object NavigateBack : StorageEvent
}
