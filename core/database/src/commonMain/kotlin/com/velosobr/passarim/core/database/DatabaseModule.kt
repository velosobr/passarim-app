// Registro do banco no Koin.

package com.velosobr.passarim.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.velosobr.passarim.core.domain.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.scope.Scope
import org.koin.dsl.module

/** Cada plataforma diz onde o arquivo do banco fica (os arquivos DatabaseModule.*.kt). */
internal expect fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase>

/**
 * Módulo Koin do banco: uma instância só do banco, do DAO e do repositório.
 * As consultas rodam no Dispatchers.IO, o conjunto de threads para trabalho de
 * disco e rede, para nunca travar a tela.
 */
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
