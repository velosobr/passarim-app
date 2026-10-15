package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.velosobr.passarim.core.domain.ImageCacheCleaner
import com.velosobr.passarim.core.domain.SettingsRepository
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal const val SETTINGS_FILE_NAME = "settings.preferences_pb"

internal expect fun Scope.settingsFilePath(): String

val settingsDataModule =
    module {
        single<DataStore<Preferences>> { PreferenceDataStoreFactory.createWithPath { settingsFilePath().toPath() } }
        single<SettingsRepository> { DataStoreSettingsRepository(get()) }
        single<ImageCacheCleaner> { CoilImageCacheCleaner(get()) }
    }
