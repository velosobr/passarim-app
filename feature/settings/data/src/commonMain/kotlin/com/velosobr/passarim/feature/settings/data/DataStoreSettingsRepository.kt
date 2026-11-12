// As preferências do usuário gravadas com o DataStore.

package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.domain.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Implementa o SettingsRepository com o DataStore Preferences, que guarda
 * pares chave/valor num arquivo e avisa por Flow a cada mudança. edit grava
 * de forma atômica (tudo ou nada). Chaves ausentes (primeira abertura) ficam
 * com os padrões de Settings().
 */
class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun observe(): Flow<Settings> = dataStore.data.map { it.toSettings() }.distinctUntilChanged()

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { it[THEME] = theme.name }
    }

    override suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { it[DYNAMIC_COLORS] = enabled }
    }

    override suspend fun setAutoPlay(enabled: Boolean) {
        dataStore.edit { it[AUTO_PLAY] = enabled }
    }

    /** Grava a jornada pelo nome do enum ("ADULT" ou "KIDS"); o edit só toca nesta chave, e as outras ficam como estão. */
    override suspend fun setJourney(journey: Journey) {
        dataStore.edit { it[JOURNEY] = journey.name }
    }

    /** Converte o que está gravado nas Settings do domínio. */
    private fun Preferences.toSettings(): Settings {
        val defaults = Settings()
        return Settings(
            theme = ThemeMode.entries.firstOrNull { it.name == this[THEME] } ?: defaults.theme,
            dynamicColors = this[DYNAMIC_COLORS] ?: defaults.dynamicColors,
            autoPlay = this[AUTO_PLAY] ?: defaults.autoPlay,
            // Sem a chave, ou com um nome que este app não conhece (um valor
            // de outra versão, por exemplo), a jornada fica nula e a Escolha
            // aparece de novo, em vez de o app quebrar ao abrir.
            journey = Journey.entries.firstOrNull { it.name == this[JOURNEY] },
        )
    }

    /** As chaves das preferências no arquivo; mudar o nome perderia o valor já gravado. */
    private companion object {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamicColors")
        val AUTO_PLAY = booleanPreferencesKey("autoPlay")
        val JOURNEY = stringPreferencesKey("journey")
    }
}
