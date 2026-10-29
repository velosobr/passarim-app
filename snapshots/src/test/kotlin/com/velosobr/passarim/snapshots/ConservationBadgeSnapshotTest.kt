// Snapshot do selo de conservação com todos os status.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.piuds.components.PiuConservationBadge
import com.velosobr.piuds.theme.LocalPiuReduceMotion
import com.velosobr.piuds.theme.PiuTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Selo de conservação em claro e escuro, com fonte 1.0×, 1.3× e 2.0×. */
@OptIn(ExperimentalResourceApi::class)
@RunWith(Parameterized::class)
class ConservationBadgeSnapshotTest(
    private val dark: Boolean,
    private val fontScalePercent: Int,
) {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig =
                DeviceConfig.PIXEL_5.copy(
                    nightMode = if (dark) NightMode.NIGHT else NightMode.NOTNIGHT,
                    fontScale = fontScalePercent / 100f,
                ),
        )

    @Before
    fun setUp() {
        // O Compose Multiplatform lê strings e fontes pelo contexto Android; no Paparazzi ninguém o inicializa.
        setResourceReaderAndroidContext(paparazzi.context)
    }

    @Test
    fun allStatuses() {
        val name = "${if (dark) "dark" else "light"}-${fontScalePercent / 100f}x"
        paparazzi.snapshot(name) {
            // Fixa "reduzir animações" para o snapshot não depender do sistema.
            CompositionLocalProvider(LocalPiuReduceMotion provides false) {
                PiuTheme(themeMode = if (dark) ThemeMode.DARK else ThemeMode.LIGHT) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ConservationStatus.entries.forEach { PiuConservationBadge(it) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        // Percentuais inteiros: um Float no nome do teste viraria "1,3" ou "1.3" conforme o locale da máquina,
        // e o nome vira nome de arquivo de referência (gravado no Mac, verificado no Linux do CI).
        @Parameterized.Parameters(name = "dark={0}, fontScale={1}%")
        fun params(): List<Array<Any>> = listOf(false, true).flatMap { dark -> listOf(100, 130, 200).map { arrayOf<Any>(dark, it) } }
    }
}
