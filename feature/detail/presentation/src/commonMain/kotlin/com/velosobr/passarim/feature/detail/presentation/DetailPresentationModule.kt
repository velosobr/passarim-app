package com.velosobr.passarim.feature.detail.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val detailPresentationModule =
    module {
        includes(audioPlayerModule)
        viewModelOf(::DetailViewModel)
    }
