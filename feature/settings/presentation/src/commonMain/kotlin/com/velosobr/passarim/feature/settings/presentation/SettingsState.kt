package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode

data class SettingsState(
    val settings: Settings = Settings(),
    val dynamicColorsSupported: Boolean = false,
    val versionName: String = "",
)

sealed interface SettingsAction {
    data class SelectTheme(
        val theme: ThemeMode,
    ) : SettingsAction

    data class ChangeDynamicColors(
        val enabled: Boolean,
    ) : SettingsAction

    data class ChangeAutoPlay(
        val enabled: Boolean,
    ) : SettingsAction

    data object OpenStorage : SettingsAction

    data object OpenAbout : SettingsAction
}

sealed interface SettingsEvent {
    data object NavigateToStorage : SettingsEvent

    data object NavigateToAbout : SettingsEvent
}
