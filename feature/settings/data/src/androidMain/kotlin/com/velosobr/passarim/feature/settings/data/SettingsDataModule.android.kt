// Onde o arquivo de preferências fica no Android.

package com.velosobr.passarim.feature.settings.data

import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

/** Na pasta de arquivos privada do app. */
internal actual fun Scope.settingsFilePath(): String = androidContext().filesDir.resolve(SETTINGS_FILE_NAME).absolutePath
