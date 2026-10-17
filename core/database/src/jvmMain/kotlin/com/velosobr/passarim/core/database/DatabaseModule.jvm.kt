package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.scope.Scope
import java.io.File

internal actual fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase> =
    Room.databaseBuilder<PassarimDatabase>(name = File(System.getProperty("java.io.tmpdir"), DATABASE_FILE_NAME).absolutePath)
