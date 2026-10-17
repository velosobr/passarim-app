// Registro dos ViewModels das Configurações no Koin.

package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.designsystem.theme.isDynamicColorSupported
import com.velosobr.passarim.core.domain.AppConfig
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * viewModel { } monta o ViewModel à mão quando ele precisa de um valor que
 * não é peça do Koin (o suporte a cores dinâmicas e a versão do app).
 */
val settingsPresentationModule =
    module {
        viewModel { SettingsViewModel(get(), isDynamicColorSupported, get<AppConfig>().versionName) }
        viewModelOf(::StorageViewModel)
        viewModel { AboutViewModel(get<AppConfig>().versionName) }
    }
