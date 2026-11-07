// Testes do PiuMotion: com "reduzir animações" ligado, toda mola vira troca
// seca. Rodam no host Android com o Robolectric (que imita o Android numa JVM
// comum), porque precisam de uma composição de verdade.

package com.velosobr.piuds

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.velosobr.piuds.theme.LocalPiuReduceMotion
import com.velosobr.piuds.theme.PiuMotion
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PiuMotionTest {
    /** Lê as duas molas dentro do tema, com "reduzir animações" no valor dado. */
    private fun specs(reduceMotion: Boolean): List<FiniteAnimationSpec<Float>> {
        val out = mutableListOf<FiniteAnimationSpec<Float>>()
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalPiuReduceMotion provides reduceMotion) {
                    PiuTheme {
                        out += PiuMotion.press<Float>()
                        out += PiuMotion.state<Float>()
                    }
                }
            }
        }
        return out
    }

    @Test
    fun springsWhenMotionIsAllowed() {
        specs(reduceMotion = false).forEach { assertThat(it).isInstanceOf<SpringSpec<Float>>() }
    }

    @Test
    fun snapsWhenReduceMotionIsOn() {
        specs(reduceMotion = true).forEach { assertThat(it).isInstanceOf<SnapSpec<Float>>() }
    }
}
