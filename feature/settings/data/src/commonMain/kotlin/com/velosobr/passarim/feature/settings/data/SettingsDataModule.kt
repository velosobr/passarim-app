// Registro do DataStore, das preferências e do limpador de cache no Koin.

package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.velosobr.passarim.core.domain.ImageCacheCleaner
import com.velosobr.passarim.core.domain.SettingsRepository
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import org.koin.dsl.module

/** Nome do arquivo das preferências; o DataStore Preferences grava em protobuf e exige a extensão .preferences_pb. */
internal const val SETTINGS_FILE_NAME = "settings.preferences_pb"

/** Cada plataforma diz onde o arquivo fica. */
internal expect fun Scope.settingsFilePath(): String

/** Uma instância só do DataStore: o DataStore exige um por arquivo no processo. */
val settingsDataModule =
    module {
        single<DataStore<Preferences>> { PreferenceDataStoreFactory.createWithPath { settingsFilePath().toPath() } }
        single<SettingsRepository> { DataStoreSettingsRepository(get()) }
        single<ImageCacheCleaner> { CoilImageCacheCleaner(get()) }
    }
