// Registro do ViewModel dos Favoritos no Koin.

package com.velosobr.passarim.feature.favorites.presentation

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** O ViewModel recebe o FavoritesRepository registrado pelo core:database. */
val favoritesPresentationModule =
    module {
        viewModelOf(::FavoritesViewModel)
    }
