package com.velosobr.passarim.feature.settings.presentation

const val PRIVACY_POLICY_URL = "https://github.com/velosobr/passarim-docs/blob/main/docs/privacidade.md"

data class AboutState(
    val versionName: String,
)

sealed interface AboutAction {
    data object Back : AboutAction

    data object OpenPrivacyPolicy : AboutAction

    data object OpenLicenses : AboutAction
}

sealed interface AboutEvent {
    data object NavigateBack : AboutEvent

    data class OpenUrl(
        val url: String,
    ) : AboutEvent

    data object NavigateToLicenses : AboutEvent
}
