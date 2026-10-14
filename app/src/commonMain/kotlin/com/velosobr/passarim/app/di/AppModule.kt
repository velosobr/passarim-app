package com.velosobr.passarim.app.di

import com.velosobr.passarim.app.favorites.InMemoryFavoritesRepository
import com.velosobr.passarim.core.data.coreDataModule
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.feature.explore.data.exploreDataModule
import com.velosobr.passarim.feature.explore.presentation.explorePresentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

/** O que só o `app` decide: os favoritos ficam em memória até a 5d trazer o Room. */
private val appModule =
    module {
        single<FavoritesRepository> { InMemoryFavoritesRepository() }
    }

/**
 * Inicia o Koin. No Android é chamado pelo `Application`; no iOS, por `MainViewController`.
 * [platform] permite à plataforma acrescentar o que é só dela (`androidContext`, por exemplo).
 */
fun initKoin(
    config: AppConfig,
    platform: KoinApplication.() -> Unit = {},
) {
    startKoin {
        platform()
        modules(module { single { config } }, coreDataModule, appModule, exploreDataModule, explorePresentationModule)
    }
}
