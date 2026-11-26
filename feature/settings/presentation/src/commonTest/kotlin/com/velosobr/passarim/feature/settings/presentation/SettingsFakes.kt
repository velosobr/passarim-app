// Peças falsas dos testes de Configurações.

package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.ImageCacheCleaner
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

/** Preferências falsas num StateFlow, que mudam como as de verdade. */
class FakeSettingsRepository(
    initial: Settings = Settings(),
) : SettingsRepository {
    val settings = MutableStateFlow(initial)

    override fun observe(): StateFlow<Settings> = settings

    override suspend fun setTheme(theme: ThemeMode) = settings.update { it.copy(theme = theme) }

    override suspend fun setDynamicColors(enabled: Boolean) = settings.update { it.copy(dynamicColors = enabled) }

    override suspend fun setAutoPlay(enabled: Boolean) = settings.update { it.copy(autoPlay = enabled) }

    override suspend fun setJourney(journey: Journey) = settings.update { it.copy(journey = journey) }
}

/** Limpador falso: conta as limpezas e pode segurar a limpeza no gate até o teste liberar. */
class FakeImageCacheCleaner : ImageCacheCleaner {
    var calls = 0
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun clearImageCache() {
        calls++
        gate?.await()
    }
}

/** Checagem do Kids falsa: devolve [result], conta as chamadas e, com [gate], espera o teste liberar. */
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
