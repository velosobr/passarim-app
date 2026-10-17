// Registro do ViewModel do Explorar no Koin.

package com.velosobr.passarim.feature.explore.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * O `SavedStateHandle` do ViewModel é fornecido pelo Koin a partir do `NavBackStackEntry`.
 * viewModelOf cria o ViewModel pelo construtor, pedindo cada parâmetro ao Koin.
 */
val explorePresentationModule =
    module {
        viewModelOf(::ExploreViewModel)
    }
