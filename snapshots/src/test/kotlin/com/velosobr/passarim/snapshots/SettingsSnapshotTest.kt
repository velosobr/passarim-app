// Snapshots das telas de Configurações.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.ScrollState
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.passarim.feature.settings.presentation.AboutScreen
import com.velosobr.passarim.feature.settings.presentation.AboutState
import com.velosobr.passarim.feature.settings.presentation.SettingsScreen
import com.velosobr.passarim.feature.settings.presentation.SettingsState
import com.velosobr.passarim.feature.settings.presentation.StorageScreen
import com.velosobr.passarim.feature.settings.presentation.StorageState
import org.junit.Test

/**
 * Configurações em várias combinações (com e sem cores dinâmicas, rolada até
 * o fim), Dados e armazenamento e Sobre. ScrollState(Int.MAX_VALUE) começa a
 * tela rolada até o fim.
 */
class SettingsSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private fun settings(
        name: String,
        state: SettingsState,
        scrollToEnd: Boolean = false,
    ) = snap(name) {
        val scroll = ScrollState(if (scrollToEnd) Int.MAX_VALUE else 0)
        SettingsScreen(state = state, onAction = {}, scrollState = scroll)
    }

    private val defaults = SettingsState(Settings(), dynamicColorsSupported = true, versionName = "1.0.0")

    @Test
    fun system() = settings("settings-system", defaults)

    @Test
    fun systemEnd() = settings("settings-system-end", defaults, scrollToEnd = true)

    @Test
    fun lightWithDynamicColors() =
        settings("settings-light-dynamic", defaults.copy(settings = Settings(theme = ThemeMode.LIGHT, dynamicColors = true)))

    @Test
    fun darkWithAutoPlay() = settings("settings-dark-autoplay", defaults.copy(settings = Settings(theme = ThemeMode.DARK, autoPlay = true)))

    @Test
    fun withoutDynamicColors() = settings("settings-ios", defaults.copy(dynamicColorsSupported = false))

    @Test
    fun storage() = snap("storage") { StorageScreen(state = StorageState.Idle, onAction = {}) }

    @Test
    fun storageCleared() = snap("storage-cleared") { StorageScreen(state = StorageState.Cleared, onAction = {}) }

    @Test
    fun about() = snap("about") { AboutScreen(state = AboutState("1.0.0"), onAction = {}) }

    @Test
    fun aboutEnd() = snap("about-end") { AboutScreen(state = AboutState("1.0.0"), onAction = {}, scrollState = ScrollState(Int.MAX_VALUE)) }
}
