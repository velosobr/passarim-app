package com.velosobr.passarim.feature.settings.presentation

sealed interface StorageState {
    data object Idle : StorageState

    data object Clearing : StorageState

    data object Cleared : StorageState
}

sealed interface StorageAction {
    data object ClearCache : StorageAction

    data object Back : StorageAction
}

sealed interface StorageEvent {
    data object NavigateBack : StorageEvent
}
