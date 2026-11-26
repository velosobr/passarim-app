// Snapshots da aba Configurações num aparelho estreito (360 dp de largura).

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.feature.settings.presentation.SettingsScreen
import com.velosobr.passarim.feature.settings.presentation.SettingsState
import org.junit.Test

/**
 * A aba Configurações no Nexus 5 (360 dp), mais estreito que o Pixel 5 (393 dp) dos outros
 * snapshots. Mostra o controle do tema empilhado quando as três opções não cabem lado a lado
 * (a 1.3×, por exemplo), em vez de cortar ou quebrar o "Escuro".
 */
class SettingsNarrowSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent, DeviceConfig.NEXUS_5) {
    @Test
    fun system() =
        snap("settings-narrow") {
            SettingsScreen(
                state = SettingsState(Settings(), dynamicColorsSupported = true, versionName = "1.0.0"),
                onAction = {},
                contentPadding = PaddingValues(bottom = 92.dp),
            )
        }
}
