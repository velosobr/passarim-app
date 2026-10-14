package com.velosobr.passarim.feature.detail.presentation

sealed interface DetailAction {
    data object Back : DetailAction

    data object Retry : DetailAction

    data object ToggleFavorite : DetailAction

    data object TogglePlay : DetailAction

    data class Seek(
        val positionMs: Long,
    ) : DetailAction

    data object ToggleCredits : DetailAction

    data class OpenCredit(
        val url: String,
    ) : DetailAction

    data object ScreenPaused : DetailAction
}

sealed interface DetailEvent {
    data object NavigateBack : DetailEvent

    data class OpenUrl(
        val url: String,
    ) : DetailEvent
}
