// O estado da tela de Detalhe.

package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail

/** O estado da tela: a fase do conteúdo, o favorito, os créditos abertos e o player. */
data class DetailState(
    val phase: DetailPhase = DetailPhase.Loading,
    val retryEnabled: Boolean = true,
    val isFavorite: Boolean = false,
    val creditsExpanded: Boolean = false,
    val player: PlaybackState = PlaybackState.Idle,
)

/** Em que ponto o conteúdo está: carregando, pronto, não encontrado ou com erro. */
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
