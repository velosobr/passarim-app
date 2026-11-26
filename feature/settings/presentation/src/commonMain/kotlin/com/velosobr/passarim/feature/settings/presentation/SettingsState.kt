// O estado, as ações e os eventos da aba Configurações.

package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.passarim.core.presentation.KidsAvailabilityState

/**
 * As preferências atuais, se o aparelho tem cores dinâmicas, a versão do app e o estado do item
 * Passarim Kids (seção "Para crianças"). O padrão de [kids] é escondido: a seção só aparece quando o
 * ViewModel pergunta ao BFF (ele começa em "conferindo").
 */
data class SettingsState(
    val settings: Settings = Settings(),
    val dynamicColorsSupported: Boolean = false,
    val versionName: String = "",
    val kids: KidsAvailabilityState = KidsAvailabilityState.HIDDEN,
)

/** Mudar o tema, as cores dinâmicas ou a reprodução automática, abrir uma subtela ou entrar no Kids. */
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

    data object OpenKids : SettingsAction
}

/** Eventos de navegação para as subtelas. */
sealed interface SettingsEvent {
    data object NavigateToStorage : SettingsEvent

    data object NavigateToAbout : SettingsEvent
}
