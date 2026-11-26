// O estado e as ações da Escolha de jornada, a primeira tela depois do
// splash na primeira abertura: Passarim (adulto) ou Passarim Kids.

package com.velosobr.passarim.feature.journey.presentation

import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.presentation.KidsAvailabilityState

/**
 * [kids] é o estado do cartão Kids (conferindo, disponível, sem rede ou escondido). O cartão do
 * adulto não tem estado: está sempre habilitado.
 */
data class JourneyChoiceState(
    val kids: KidsAvailabilityState = KidsAvailabilityState.CHECKING,
)

/**
 * Tocar num cartão ou no "Tentar de novo". Não há eventos de navegação: gravar a jornada já faz a
 * raiz do app abrir a jornada escolhida.
 */
sealed interface JourneyChoiceAction {
    data class ChooseJourney(
        val journey: Journey,
    ) : JourneyChoiceAction

    data object RetryKids : JourneyChoiceAction
}
