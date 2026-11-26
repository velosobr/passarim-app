// Registro do DataStore, das preferências e do limpador de cache no Koin.

package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.velosobr.passarim.core.domain.ImageCacheCleaner
import com.velosobr.passarim.core.domain.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import org.koin.dsl.module

/** Nome do arquivo das preferências; o DataStore Preferences grava em protobuf e exige a extensão .preferences_pb. */
internal const val SETTINGS_FILE_NAME = "settings.preferences_pb"

/** Cada plataforma diz onde o arquivo fica. */
internal expect fun Scope.settingsFilePath(): String

/**
 * Cria o DataStore das preferências no arquivo que produceFile indica. O
 * scope é onde o DataStore roda as leituras e gravações: no app, um escopo
 * próprio no Dispatchers.IO que vive o processo inteiro (o SupervisorJob
 * impede que um erro numa gravação cancele o escopo); nos testes, o do teste.
 *
 * O ReplaceFileCorruptionHandler entra em ação quando o arquivo existe mas
 * não é um protobuf válido (gravação interrompida, por exemplo): troca o
 * conteúdo por preferências vazias, e o app volta aos padrões de Settings()
 * (inclusive a jornada nula, que mostra a Escolha) em vez de falhar a cada
 * leitura e a cada gravação.
 */
internal fun createSettingsDataStore(
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    produceFile: () -> Path,
): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        scope = scope,
        produceFile = produceFile,
    )

/** Uma instância só do DataStore: o DataStore exige um por arquivo no processo. */
val settingsDataModule =
    module {
        single<DataStore<Preferences>> { createSettingsDataStore { settingsFilePath().toPath() } }
        single<SettingsRepository> { DataStoreSettingsRepository(get()) }
        single<ImageCacheCleaner> { CoilImageCacheCleaner(get()) }
    }
