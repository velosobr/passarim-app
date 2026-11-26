// O ViewModel da Escolha: pergunta ao BFF se o Kids existe ao abrir (e no
// "Tentar de novo") e grava a jornada escolhida.

package com.velosobr.passarim.feature.journey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.KidsAvailabilityRepository
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import com.velosobr.passarim.core.presentation.toKidsAvailabilityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A Escolha. A checagem do Kids não é gravada: roda a cada abertura da tela (o bloco init roda quando
 * o ViewModel é criado), porque o BFF pode ganhar ou perder o Kids entre uma abertura e outra.
 */
class JourneyChoiceViewModel(
    private val settings: SettingsRepository,
    private val kidsAvailability: KidsAvailabilityRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(JourneyChoiceState())
    val state: StateFlow<JourneyChoiceState> = mutableState.asStateFlow()

    init {
        checkKids()
    }

    /** Trata cada toque da tela. */
    fun onAction(action: JourneyChoiceAction) {
        when (action) {
            is JourneyChoiceAction.ChooseJourney -> choose(action.journey)
            JourneyChoiceAction.RetryKids -> checkKids()
        }
    }

    /**
     * O adulto entra sempre; o Kids só quando a checagem disse que ele existe. O cartão desabilitado
     * já não chama isto, mas o ViewModel confere de novo para não depender só da tela.
     */
    private fun choose(journey: Journey) {
        if (journey == Journey.KIDS && !mutableState.value.kids.canEnter) return
        viewModelScope.launch { settings.setJourney(journey) }
    }

    /** Volta para "conferindo" e pergunta ao BFF; a resposta vira um dos quatro estados do cartão. */
    private fun checkKids() {
        mutableState.update { it.copy(kids = KidsAvailabilityState.CHECKING) }
        viewModelScope.launch {
            val kids = kidsAvailability.checkKidsAvailability().toKidsAvailabilityState()
            mutableState.update { it.copy(kids = kids) }
        }
    }
}
