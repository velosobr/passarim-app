package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class Settings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = false,
    val autoPlay: Boolean = false,
)

interface SettingsRepository {
    fun observe(): Flow<Settings>

    suspend fun setTheme(theme: ThemeMode)

    suspend fun setDynamicColors(enabled: Boolean)

    suspend fun setAutoPlay(enabled: Boolean)
}
