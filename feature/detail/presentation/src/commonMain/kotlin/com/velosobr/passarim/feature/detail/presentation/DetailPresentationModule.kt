// Registro do ViewModel do Detalhe (e do player) no Koin.

package com.velosobr.passarim.feature.detail.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** includes junta o módulo do player, que cada plataforma define. */
val detailPresentationModule =
    module {
        includes(audioPlayerModule)
        viewModelOf(::DetailViewModel)
    }
