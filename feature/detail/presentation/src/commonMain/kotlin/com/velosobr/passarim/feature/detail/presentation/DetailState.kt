package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail

data class DetailState(
    val phase: DetailPhase = DetailPhase.Loading,
    val retryEnabled: Boolean = true,
    val isFavorite: Boolean = false,
    val creditsExpanded: Boolean = false,
    val player: PlaybackState = PlaybackState.Idle,
)

sealed interface DetailPhase {
    data object Loading : DetailPhase

    data class Content(
        val detail: SpeciesDetail,
    ) : DetailPhase

    data object NotFound : DetailPhase

    data class Error(
        val error: ScreenError,
    ) : DetailPhase
}
