package com.velosobr.passarim.feature.settings.data

import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.settingsFilePath(): String = androidContext().filesDir.resolve(SETTINGS_FILE_NAME).absolutePath
