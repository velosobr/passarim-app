// O estado, as ações e os eventos da aba Configurações.

package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode

/** As preferências atuais, se o aparelho tem cores dinâmicas e a versão do app. */
data class SettingsState(
    val settings: Settings = Settings(),
    val dynamicColorsSupported: Boolean = false,
    val versionName: String = "",
)

/** Mudar o tema, as cores dinâmicas ou a reprodução automática, ou abrir uma subtela. */
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

/** Eventos de navegação para as subtelas. */
sealed interface SettingsEvent {
    data object NavigateToStorage : SettingsEvent

    data object NavigateToAbout : SettingsEvent
}
