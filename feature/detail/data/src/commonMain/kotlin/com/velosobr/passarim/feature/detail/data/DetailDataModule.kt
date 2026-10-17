// Registro do repositório do Detalhe no Koin.

package com.velosobr.passarim.feature.detail.data

import com.velosobr.passarim.feature.detail.domain.DetailRepository
import org.koin.dsl.module

/** O HttpClient e o MediaUrlRewriter vêm do coreDataModule. */
val detailDataModule =
    module {
        single<DetailRepository> { RemoteDetailRepository(get(), get()) }
    }
