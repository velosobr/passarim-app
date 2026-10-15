package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase> {
    val context = androidContext().applicationContext
    return Room.databaseBuilder<PassarimDatabase>(context, context.getDatabasePath(DATABASE_FILE_NAME).absolutePath)
}
