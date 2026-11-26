// O ViewModel da trava para adultos: guarda a conta e os algarismos
// digitados, confere a resposta e, no acerto, grava a jornada do adulto.

package com.velosobr.passarim.feature.journey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Até 3 algarismos: a maior resposta possível é 99 × 9 = 891. */
private const val MAX_ANSWER_DIGITS = 3

/**
 * A trava. [random] sorteia as contas (no app, o Random de verdade; nos testes, um com semente).
 *
 * Nada é guardado no SavedStateHandle de propósito: se o sistema matar o app com a trava aberta, ela
 * volta com uma conta nova e sem os algarismos (spec da frente D, 4.3), e a jornada continua KIDS,
 * porque só o acerto grava alguma coisa.
 *
 * MutableStateFlow é um fluxo que sempre tem um valor atual; update troca esse valor a partir do
 * anterior de forma segura. A tela observa o [state] somente leitura (asStateFlow).
 */
class KidsGateViewModel(
    private val settings: SettingsRepository,
    private val random: Random,
) : ViewModel() {
    private val mutableState = MutableStateFlow(KidsGateState(question = drawGateQuestion(random)))
    val state: StateFlow<KidsGateState> = mutableState.asStateFlow()

    // Channel guarda o evento até a tela coletar; receiveAsFlow entrega cada evento a um coletor só.
    private val eventChannel = Channel<KidsGateEvent>(Channel.BUFFERED)
    val events: Flow<KidsGateEvent> = eventChannel.receiveAsFlow()

    /** Trata cada tecla e o "Voltar". */
    fun onAction(action: KidsGateAction) {
        when (action) {
            is KidsGateAction.TypeDigit ->
                mutableState.update { if (it.answer.length < MAX_ANSWER_DIGITS) it.copy(answer = it.answer + action.digit) else it }
            KidsGateAction.Erase -> mutableState.update { it.copy(answer = it.answer.dropLast(1)) }
            KidsGateAction.Confirm -> confirm()
            KidsGateAction.Back -> eventChannel.trySend(KidsGateEvent.NavigateBack)
        }
    }

    /**
     * Confere a resposta. Sem algarismos, o toque não conta (nem como erro). Acertou: grava ADULT, e
     * a raiz do app, que observa a jornada, troca para o adulto. Errou: mostra a mensagem e sorteia
     * outra conta, sem limite de tentativas e sem espera.
     */
    private fun confirm() {
        val current = mutableState.value
        if (current.answer.isEmpty()) return
        if (current.answer.toInt() == current.question.answer) {
            viewModelScope.launch { settings.setJourney(Journey.ADULT) }
        } else {
            mutableState.update { KidsGateState(question = drawGateQuestion(random, previous = it.question), wrongAnswer = true) }
        }
    }
}
