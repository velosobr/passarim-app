package com.velosobr.passarim.snapshots

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.velosobr.passarim.core.designsystem.theme.PassarimTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Base dos snapshots de tela: cada teste roda em claro e escuro × fonte 1.0×, 1.3× e 2.0× (seis
 * referências por chamada de [snap]). O JUnit acha o `@Parameters` herdado desta classe.
 */
@OptIn(ExperimentalResourceApi::class)
@RunWith(Parameterized::class)
abstract class ThemedSnapshotTest(
    private val dark: Boolean,
    private val fontScalePercent: Int,
    device: DeviceConfig = DeviceConfig.PIXEL_5,
) {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig =
                device.copy(
                    nightMode = if (dark) NightMode.NIGHT else NightMode.NOTNIGHT,
                    fontScale = fontScalePercent / 100f,
                ),
        )

    @Before
    fun setUpResources() {
        // O Compose Multiplatform lê strings e fontes pelo contexto Android; no Paparazzi ninguém o inicializa.
        setResourceReaderAndroidContext(paparazzi.context)
    }

    /** Fotografa [content] dentro do tema, com o nome `<nome>-<claro|escuro>-<escala>x`. */
    fun snap(
        name: String,
        content: @Composable () -> Unit,
    ) {
        paparazzi.snapshot("$name-${if (dark) "dark" else "light"}-${fontScalePercent / 100f}x") {
            PassarimTheme(darkTheme = dark) {
                Surface(color = MaterialTheme.colorScheme.background, content = content)
            }
        }
    }

    companion object {
        @JvmStatic
        // Percentuais inteiros: um Float no nome do teste viraria "1,3" ou "1.3" conforme o locale da máquina.
        @Parameterized.Parameters(name = "dark={0}, fontScale={1}%")
        fun params(): List<Array<Any>> = listOf(false, true).flatMap { dark -> listOf(100, 130, 200).map { arrayOf<Any>(dark, it) } }
    }
}
