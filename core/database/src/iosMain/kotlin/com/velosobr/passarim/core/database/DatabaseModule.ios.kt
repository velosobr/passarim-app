// Onde o banco fica no iOS: na pasta Documents do app.

package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.scope.Scope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * No iOS, o caminho vem do NSFileManager (a API de arquivos da Apple). As
 * classes do Foundation chegam ao Kotlin pela interoperabilidade com
 * Objective-C, por isso o @OptIn(ExperimentalForeignApi).
 */
@OptIn(ExperimentalForeignApi::class)
internal actual fun Scope.passarimDatabaseBuilder(): RoomDatabase.Builder<PassarimDatabase> {
    val documents =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )
    return Room.databaseBuilder<PassarimDatabase>(name = requireNotNull(documents?.path) + "/" + DATABASE_FILE_NAME)
}
