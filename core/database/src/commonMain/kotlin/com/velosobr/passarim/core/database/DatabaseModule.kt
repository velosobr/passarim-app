package com.velosobr.passarim.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.velosobr.passarim.core.domain.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal expect fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase>

val databaseModule =
    module {
        single<PassarimDatabase> {
            passarimDatabaseBuilder()
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
        single { get<PassarimDatabase>().favoriteDao() }
        single<FavoritesRepository> { LocalFavoritesRepository(get()) }
    }
