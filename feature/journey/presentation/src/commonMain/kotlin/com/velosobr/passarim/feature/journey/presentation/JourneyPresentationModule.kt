// Registro dos ViewModels da Escolha e da trava no Koin. O app junta este
// módulo aos outros no initKoin (AppModule.kt).

package com.velosobr.passarim.feature.journey.presentation

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.random.Random

/**
 * viewModelOf monta o ViewModel pedindo ao Koin cada parâmetro do construtor (as preferências e a
 * checagem do Kids). A trava é montada à mão porque o Random não é peça do Koin: no app, vale o
 * aleatório padrão do Kotlin (Random.Default).
 */
val journeyPresentationModule =
    module {
        viewModelOf(::JourneyChoiceViewModel)
        viewModel { KidsGateViewModel(get(), Random.Default) }
    }
