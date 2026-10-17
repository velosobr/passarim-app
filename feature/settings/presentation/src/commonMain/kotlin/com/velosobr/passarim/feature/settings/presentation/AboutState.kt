// O estado, as ações e os eventos da tela Sobre.

package com.velosobr.passarim.feature.settings.presentation

/** A política de privacidade, publicada no repositório de documentação. */
const val PRIVACY_POLICY_URL = "https://github.com/velosobr/passarim-docs/blob/main/docs/privacidade.md"

/** A versão do app mostrada no Sobre. */
data class AboutState(
    val versionName: String,
)

/** Voltar, abrir a política de privacidade ou abrir as licenças. */
sealed interface AboutAction {
    data object Back : AboutAction

    data object OpenPrivacyPolicy : AboutAction

    data object OpenLicenses : AboutAction
}

/** Eventos de uma vez: voltar, abrir uma URL ou ir às licenças. */
sealed interface AboutEvent {
    data object NavigateBack : AboutEvent

    data class OpenUrl(
        val url: String,
    ) : AboutEvent

    data object NavigateToLicenses : AboutEvent
}
