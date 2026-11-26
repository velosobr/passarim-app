// O ViewModel da aba Configurações. Além das preferências, pergunta ao BFF se
// o Passarim Kids existe, para mostrar (ou não) a seção "Para crianças".

package com.velosobr.passarim.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.KidsAvailabilityRepository
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import com.velosobr.passarim.core.presentation.toKidsAvailabilityState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Observa as preferências e grava cada mudança no repositório. A checagem do Kids roda ao abrir a
 * aba (o init roda quando o ViewModel é criado) e não é gravada. combine junta os dois fluxos (as
 * preferências e o estado do Kids) num estado só, recalculado quando qualquer um muda.
 */
class SettingsViewModel(
    private val repository: SettingsRepository,
    private val kidsAvailability: KidsAvailabilityRepository,
    dynamicColorsSupported: Boolean,
    versionName: String,
) : ViewModel() {
    private val initial =
        SettingsState(dynamicColorsSupported = dynamicColorsSupported, versionName = versionName, kids = KidsAvailabilityState.CHECKING)

    private val kids = MutableStateFlow(KidsAvailabilityState.CHECKING)

    val state: StateFlow<SettingsState> =
        combine(repository.observe(), kids) { settings, kids -> initial.copy(settings = settings, kids = kids) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, initial)

    private val eventChannel = Channel<SettingsEvent>(Channel.BUFFERED)
    val events: Flow<SettingsEvent> = eventChannel.receiveAsFlow()

    /** Trata cada ação da tela. */
    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SelectTheme -> viewModelScope.launch { repository.setTheme(action.theme) }
            is SettingsAction.ChangeDynamicColors -> viewModelScope.launch { repository.setDynamicColors(action.enabled) }
            is SettingsAction.ChangeAutoPlay -> viewModelScope.launch { repository.setAutoPlay(action.enabled) }
            SettingsAction.OpenStorage -> eventChannel.trySend(SettingsEvent.NavigateToStorage)
            SettingsAction.OpenAbout -> eventChannel.trySend(SettingsEvent.NavigateToAbout)
            SettingsAction.OpenKids -> openKids()
        }
    }

    init {
        checkKids()
    }

    /**
     * Disponível: grava KIDS na hora, sem confirmação, e a raiz do app abre o Kids (o tema e a
     * reprodução automática do adulto ficam como estão). Sem rede: confere de novo. Conferindo ou
     * escondido: nada.
     */
    private fun openKids() {
        when (kids.value) {
            KidsAvailabilityState.AVAILABLE -> viewModelScope.launch { repository.setJourney(Journey.KIDS) }
            KidsAvailabilityState.OFFLINE -> checkKids()
            KidsAvailabilityState.CHECKING, KidsAvailabilityState.HIDDEN -> Unit
        }
    }

    /** Pergunta ao BFF se o Kids existe, passando por "conferindo". */
    private fun checkKids() {
        kids.value = KidsAvailabilityState.CHECKING
        viewModelScope.launch { kids.update { kidsAvailability.checkKidsAvailability().toKidsAvailabilityState() } }
    }
}
