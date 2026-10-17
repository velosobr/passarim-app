// As ações da tela de Detalhe e os eventos de uma vez que o ViewModel devolve.

package com.velosobr.passarim.feature.detail.presentation

/** Tudo o que o usuário pode fazer no Detalhe. */
sealed interface DetailAction {
    /** Botão de voltar. */
    data object Back : DetailAction

    /** "Tentar novamente" da tela de erro. */
    data object Retry : DetailAction

    /** O coração do topo. */
    data object ToggleFavorite : DetailAction

    /** Tocar ou pausar o canto. */
    data object TogglePlay : DetailAction

    /** Arrastar a barra de progresso do canto. */
    data class Seek(
        val positionMs: Long,
    ) : DetailAction

    /** Abrir ou fechar a seção de créditos. */
    data object ToggleCredits : DetailAction

    /** Toque num link de crédito. */
    data class OpenCredit(
        val url: String,
    ) : DetailAction

    /** A tela foi para segundo plano: o canto para. */
    data object ScreenPaused : DetailAction
}

/** Eventos de uma vez: voltar e abrir um link no navegador. */
sealed interface DetailEvent {
    data object NavigateBack : DetailEvent

    data class OpenUrl(
        val url: String,
    ) : DetailEvent
}
