package com.velosobr.passarim.feature.favorites.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val favoritesPresentationModule =
    module {
        viewModelOf(::FavoritesViewModel)
    }
