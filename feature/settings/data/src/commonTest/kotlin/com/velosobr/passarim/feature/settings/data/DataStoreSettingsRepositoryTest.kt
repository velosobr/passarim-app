// Testes do repositório de preferências com um DataStore de verdade, num
// arquivo temporário apagado depois de cada teste.

package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.velosobr.passarim.core.domain.Journey
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

    /** dataStore cria o DataStore no arquivo temporário, preso ao escopo do teste. */
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

    @Test
    fun `sem arquivo a jornada e nula`() =
        runTest {
            val repository = DataStoreSettingsRepository(backgroundScope.dataStore())

            assertThat(repository.observe().first().journey).isNull()
        }

    @Test
    fun `grava a jornada sem mexer nas outras opcoes`() =
        runTest {
            val repository = DataStoreSettingsRepository(backgroundScope.dataStore())
            repository.setTheme(ThemeMode.DARK)
            repository.setDynamicColors(true)
            repository.setAutoPlay(true)

            repository.setJourney(Journey.KIDS)

            assertThat(repository.observe().first())
                .isEqualTo(Settings(ThemeMode.DARK, dynamicColors = true, autoPlay = true, journey = Journey.KIDS))
        }

    @Test
    fun `trocar de jornada substitui a anterior`() =
        runTest {
            val repository = DataStoreSettingsRepository(backgroundScope.dataStore())

            repository.setJourney(Journey.KIDS)
            repository.setJourney(Journey.ADULT)

            assertThat(repository.observe().first().journey).isEqualTo(Journey.ADULT)
        }

    // Confere também as outras chaves: uma leitura que caísse em Settings()
    // inteiro diante do valor desconhecido apagaria o tema e o autoPlay.
    @Test
    fun `jornada desconhecida no arquivo vira nula e as outras chaves continuam`() =
        runTest {
            val dataStore = backgroundScope.dataStore()
            dataStore.edit {
                it[stringPreferencesKey("theme")] = "DARK"
                it[booleanPreferencesKey("autoPlay")] = true
                it[stringPreferencesKey("journey")] = "TEEN"
            }

            assertThat(DataStoreSettingsRepository(dataStore).observe().first())
                .isEqualTo(Settings(ThemeMode.DARK, autoPlay = true, journey = null))
        }

    @Test
    fun `gravar outra opcao depois da jornada mantem a jornada`() =
        runTest {
            val repository = DataStoreSettingsRepository(backgroundScope.dataStore())

            repository.setJourney(Journey.KIDS)
            repository.setTheme(ThemeMode.DARK)

            assertThat(repository.observe().first().journey).isEqualTo(Journey.KIDS)
        }
}
