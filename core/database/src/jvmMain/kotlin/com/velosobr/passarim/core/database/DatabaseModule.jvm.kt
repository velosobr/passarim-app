// Onde o banco fica na JVM (testes): na pasta temporária do sistema.

package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.scope.Scope
import java.io.File

/** Na JVM, o arquivo vai para a pasta temporária. */
internal actual fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase> =
    Room.databaseBuilder<PassarimDatabase>(name = File(System.getProperty("java.io.tmpdir"), DATABASE_FILE_NAME).absolutePath)
