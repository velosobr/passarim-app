package com.velosobr.passarim.app.di

import com.velosobr.passarim.app.images.imageLoaderModule
import com.velosobr.passarim.core.data.coreDataModule
import com.velosobr.passarim.core.database.databaseModule
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.feature.detail.data.detailDataModule
import com.velosobr.passarim.feature.detail.presentation.detailPresentationModule
import com.velosobr.passarim.feature.explore.data.exploreDataModule
import com.velosobr.passarim.feature.explore.presentation.explorePresentationModule
import com.velosobr.passarim.feature.favorites.presentation.favoritesPresentationModule
import com.velosobr.passarim.feature.settings.data.settingsDataModule
import com.velosobr.passarim.feature.settings.presentation.settingsPresentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

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
        modules(
            module { single { config } },
            coreDataModule,
            databaseModule,
            imageLoaderModule,
            settingsDataModule,
            exploreDataModule,
            explorePresentationModule,
            detailDataModule,
            detailPresentationModule,
            favoritesPresentationModule,
            settingsPresentationModule,
        )
    }
}
