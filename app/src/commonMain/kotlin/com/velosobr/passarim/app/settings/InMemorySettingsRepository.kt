package com.velosobr.passarim.app.settings

import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySettingsRepository : SettingsRepository {
    private val settings = MutableStateFlow(Settings())

    override fun observe(): Flow<Settings> = settings.asStateFlow()
}
