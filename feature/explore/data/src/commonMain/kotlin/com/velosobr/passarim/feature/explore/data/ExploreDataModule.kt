package com.velosobr.passarim.feature.explore.data

import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.feature.explore.domain.ExploreRepository
import org.koin.dsl.module

/** Repositórios de Explorar. O `HttpClient` e o `MediaUrlRewriter` vêm do `coreDataModule`. */
val exploreDataModule =
    module {
        single<ExploreRepository> { RemoteExploreRepository(get(), get()) }
        single<FiltersRepository> { RemoteFiltersRepository(get()) }
    }
