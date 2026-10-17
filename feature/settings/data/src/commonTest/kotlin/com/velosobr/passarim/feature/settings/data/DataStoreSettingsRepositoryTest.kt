package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test

class DataStoreSettingsRepositoryTest {
    private val file: Path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "settings-${Random.nextLong()}.preferences_pb"

    @AfterTest
    fun deleteFile() = FileSystem.SYSTEM.delete(file)

    private fun CoroutineScope.dataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(scope = this) { file }

    @Test
    fun `sem arquivo usa os padroes`() =
        runTest {
            val repository = DataStoreSettingsRepository(backgroundScope.dataStore())

            assertThat(repository.observe().first()).isEqualTo(Settings(ThemeMode.SYSTEM, dynamicColors = false, autoPlay = false))
        }

    @Test
    fun `grava cada opcao`() =
        runTest {
            val repository = DataStoreSettingsRepository(backgroundScope.dataStore())

            repository.setTheme(ThemeMode.DARK)
            repository.setDynamicColors(true)
            repository.setAutoPlay(true)

            assertThat(repository.observe().first()).isEqualTo(Settings(ThemeMode.DARK, dynamicColors = true, autoPlay = true))
        }

    @Test
    fun `tema invalido no arquivo cai no padrao`() =
        runTest {
            val dataStore = backgroundScope.dataStore()
            dataStore.edit { it[stringPreferencesKey("theme")] = "PURPLE" }

            assertThat(DataStoreSettingsRepository(dataStore).observe().first().theme).isEqualTo(ThemeMode.SYSTEM)
        }
}
