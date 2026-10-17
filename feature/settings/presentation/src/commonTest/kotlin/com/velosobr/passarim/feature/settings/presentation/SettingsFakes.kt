package com.velosobr.passarim.feature.settings.presentation

import com.velosobr.passarim.core.domain.ImageCacheCleaner
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.domain.ThemeMode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepository(
    initial: Settings = Settings(),
) : SettingsRepository {
    val settings = MutableStateFlow(initial)

    override fun observe(): StateFlow<Settings> = settings

    override suspend fun setTheme(theme: ThemeMode) = settings.update { it.copy(theme = theme) }

    override suspend fun setDynamicColors(enabled: Boolean) = settings.update { it.copy(dynamicColors = enabled) }

    override suspend fun setAutoPlay(enabled: Boolean) = settings.update { it.copy(autoPlay = enabled) }
}

class FakeImageCacheCleaner : ImageCacheCleaner {
    var calls = 0
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun clearImageCache() {
        calls++
        gate?.await()
    }
}
