package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

data class Settings(
    val autoPlay: Boolean = false,
)

interface SettingsRepository {
    fun observe(): Flow<Settings>
}
