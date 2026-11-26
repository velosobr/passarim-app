// O estado, as ações e os eventos da trava para adultos, a tela que guarda a
// saída do Passarim Kids com uma conta de multiplicação.

package com.velosobr.passarim.feature.journey.presentation

/**
 * [question] é a conta da vez; [answer], os algarismos digitados (até 3, porque 99 × 9 = 891);
 * [wrongAnswer] mostra o "Ops, essa não. Tente esta!" depois de um erro.
 */
data class KidsGateState(
    val question: GateQuestion,
    val answer: String = "",
    val wrongAnswer: Boolean = false,
)

/** As teclas do teclado da trava e o botão "Voltar". */
sealed interface KidsGateAction {
    data class TypeDigit(
        val digit: Int,
    ) : KidsGateAction

    data object Erase : KidsGateAction

    data object Confirm : KidsGateAction

    data object Back : KidsGateAction
}

/** Fechar a trava e ficar no Kids. O acerto não é evento: gravar ADULT já faz a raiz trocar de jornada. */
sealed interface KidsGateEvent {
    data object NavigateBack : KidsGateEvent
}
