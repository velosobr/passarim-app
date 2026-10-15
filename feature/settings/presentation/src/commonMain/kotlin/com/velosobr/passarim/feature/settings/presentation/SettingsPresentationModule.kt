package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.designsystem.theme.isDynamicColorSupported
import com.velosobr.passarim.core.domain.AppConfig
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsPresentationModule =
    module {
        viewModel { SettingsViewModel(get(), isDynamicColorSupported, get<AppConfig>().versionName) }
        viewModelOf(::StorageViewModel)
        viewModel { AboutViewModel(get<AppConfig>().versionName) }
    }
