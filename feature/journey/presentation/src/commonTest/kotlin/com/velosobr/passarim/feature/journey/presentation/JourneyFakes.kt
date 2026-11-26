// Peças falsas dos testes da Escolha e da trava: as preferências num
// StateFlow e uma checagem do Kids com a resposta escolhida pelo teste.

package com.velosobr.passarim.feature.journey.presentation

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.KidsAvailabilityRepository
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.domain.ThemeMode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Preferências falsas que mudam como as de verdade; [journeyWrites] conta as gravações da jornada. */
class FakeSettingsRepository(
    initial: Settings = Settings(),
) : SettingsRepository {
    val settings = MutableStateFlow(initial)
    var journeyWrites = 0

    override fun observe(): StateFlow<Settings> = settings

    override suspend fun setTheme(theme: ThemeMode) = settings.update { it.copy(theme = theme) }

    override suspend fun setDynamicColors(enabled: Boolean) = settings.update { it.copy(dynamicColors = enabled) }

    override suspend fun setAutoPlay(enabled: Boolean) = settings.update { it.copy(autoPlay = enabled) }

    override suspend fun setJourney(journey: Journey) {
        journeyWrites++
        settings.update { it.copy(journey = journey) }
    }
}

/**
 * Checagem falsa: devolve [result] e conta as chamadas. Com [gate], a resposta espera o teste
 * liberar, para o teste ver o estado "conferindo".
 */
class FakeKidsAvailabilityRepository(
    var result: Result<Boolean, DataError.Network> = Result.Success(true),
) : KidsAvailabilityRepository {
    var calls = 0
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun checkKidsAvailability(): Result<Boolean, DataError.Network> {
        calls++
        gate?.await()
        return result
    }
}
