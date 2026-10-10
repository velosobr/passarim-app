package com.velosobr.passarim.core.data

import org.koin.dsl.module

/** Módulo Koin da camada de rede. O `AppConfig` vem do módulo do `app`. */
val coreDataModule =
    module {
        single { createHttpClient(platformHttpEngine(), get()) }
        single { MediaUrlRewriter(get()) }
    }
