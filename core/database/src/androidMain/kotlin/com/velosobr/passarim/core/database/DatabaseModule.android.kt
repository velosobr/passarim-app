// Onde o banco fica no Android: na pasta de bancos do app.

package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

/** No Android, o Room usa o Context do app, que o Koin fornece (androidContext). */
internal actual fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase> {
    val context = androidContext().applicationContext
    return Room.databaseBuilder<PassarimDatabase>(context, context.getDatabasePath(DATABASE_FILE_NAME).absolutePath)
}
