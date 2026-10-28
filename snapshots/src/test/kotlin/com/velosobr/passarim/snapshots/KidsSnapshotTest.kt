// A base dos snapshots do Piu Kids: só tema claro (o Kids não tem escuro) e
// três escalas de fonte.

package com.velosobr.passarim.snapshots

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.theme.LocalPiuReduceMotion
import com.velosobr.piuds.theme.PiuTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.setResourceReaderAndroidContext
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Cada teste roda com fonte 1.0×, 1.3× e 2.0× (três referências por chamada de [snap]). */
@OptIn(ExperimentalResourceApi::class)
@RunWith(Parameterized::class)
abstract class KidsSnapshotTest(
    private val fontScalePercent: Int,
) {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5.copy(fontScale = fontScalePercent / 100f))

    @Before
    fun setUpResources() {
        // O Compose Multiplatform lê strings e fontes pelo contexto Android; no Paparazzi ninguém o inicializa.
        setResourceReaderAndroidContext(paparazzi.context)
    }

    /** Fotografa [content] no tema Kids, com o nome `<nome>-kids-<escala>x`. */
    fun snap(
        name: String,
        content: @Composable () -> Unit,
    ) {
        paparazzi.snapshot("$name-kids-${fontScalePercent / 100f}x") {
            CompositionLocalProvider(LocalPiuReduceMotion provides false) {
                PiuTheme(journey = Journey.KIDS) {
                    Surface(color = MaterialTheme.colorScheme.background, content = content)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        // Percentuais inteiros: um Float no nome do teste viraria "1,3" ou "1.3" conforme o locale da máquina.
        @Parameterized.Parameters(name = "fontScale={0}%")
        fun params(): List<Array<Any>> = listOf(100, 130, 200).map { arrayOf<Any>(it) }
    }
}
