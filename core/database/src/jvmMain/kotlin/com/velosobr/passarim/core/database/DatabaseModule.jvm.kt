// Onde o banco ficaria na JVM: na pasta temporária do sistema.

package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.scope.Scope
import java.io.File

/** Exigido porque todo expect precisa de um actual por alvo; os testes JVM usam banco em memória e não passam por aqui. */
internal actual fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase> =
    Room.databaseBuilder<PassarimDatabase>(name = File(System.getProperty("java.io.tmpdir"), DATABASE_FILE_NAME).absolutePath)
