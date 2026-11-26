// Teste do módulo Koin da Escolha e da trava: com as preferências e a
// checagem do Kids registradas (aqui, falsas), os dois ViewModels saem do Koin.

package com.velosobr.passarim.feature.journey.presentation

import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.velosobr.passarim.core.domain.KidsAvailabilityRepository
import com.velosobr.passarim.core.domain.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JourneyPresentationModuleTest {
    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `a Escolha e a trava saem do Koin`() {
        val koin =
            koinApplication {
                modules(
                    module {
                        single<SettingsRepository> { FakeSettingsRepository() }
                        single<KidsAvailabilityRepository> { FakeKidsAvailabilityRepository() }
                    },
                    journeyPresentationModule,
                )
            }.koin
        assertThat(koin.get<JourneyChoiceViewModel>()).isInstanceOf<JourneyChoiceViewModel>()
        assertThat(koin.get<KidsGateViewModel>()).isInstanceOf<KidsGateViewModel>()
    }
}
