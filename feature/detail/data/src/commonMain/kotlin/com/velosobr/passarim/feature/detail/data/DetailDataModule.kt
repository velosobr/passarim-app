package com.velosobr.passarim.feature.detail.data

import com.velosobr.passarim.feature.detail.domain.DetailRepository
import org.koin.dsl.module

val detailDataModule =
    module {
        single<DetailRepository> { RemoteDetailRepository(get(), get()) }
    }
