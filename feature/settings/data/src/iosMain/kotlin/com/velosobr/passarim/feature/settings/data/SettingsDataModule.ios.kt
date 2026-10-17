// Onde o arquivo de preferências fica no iOS.

package com.velosobr.passarim.feature.settings.data

import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.scope.Scope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** Na pasta Documents do app, achada pelo NSFileManager. */
@OptIn(ExperimentalForeignApi::class)
internal actual fun Scope.settingsFilePath(): String {
    val documents =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )
    return requireNotNull(documents?.path) + "/" + SETTINGS_FILE_NAME
}
