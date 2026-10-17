package com.velosobr.passarim.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.domain.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

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

    private fun Preferences.toSettings(): Settings {
        val defaults = Settings()
        return Settings(
            theme = ThemeMode.entries.firstOrNull { it.name == this[THEME] } ?: defaults.theme,
            dynamicColors = this[DYNAMIC_COLORS] ?: defaults.dynamicColors,
            autoPlay = this[AUTO_PLAY] ?: defaults.autoPlay,
        )
    }

    private companion object {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamicColors")
        val AUTO_PLAY = booleanPreferencesKey("autoPlay")
    }
}
