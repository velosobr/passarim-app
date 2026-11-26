// Registro do banco no Koin.
// Koin: "single" cria uma instância só para o app, e get() pede ao Koin uma
// peça já registrada.

package com.velosobr.passarim.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.WorldCountsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.scope.Scope
import org.koin.dsl.module

/** Cada plataforma diz onde o arquivo do banco fica (os arquivos DatabaseModule.*.kt). */
internal expect fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase>

/**
 * Módulo Koin do banco: uma instância só do banco, dos DAOs e dos repositórios
 * (os favoritos e a contagem dos mundos do Kids).
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
        single { get<PassarimDatabase>().kidsWorldCountDao() }
        // O FiltersRepository vem do exploreDataModule; o Koin só o procura quando alguém pede
        // a contagem, e aí os dois módulos já estão carregados.
        single<WorldCountsRepository> { LocalWorldCountsRepository(get(), get()) }
    }
