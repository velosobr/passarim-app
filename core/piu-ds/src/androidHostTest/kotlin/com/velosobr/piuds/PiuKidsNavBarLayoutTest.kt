// Teste de medida da barra do Kids com o botão do fim ("Para adultos"): com
// fonte 2.0×, o botão não pode tomar a largura das abas. GraphicsMode NATIVE
// faz o Robolectric medir o texto com a fonte de verdade (veja
// SettingsScreenTest).

package com.velosobr.piuds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import assertk.assertThat
import assertk.assertions.isLessThanOrEqualTo
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.components.kids.PiuKidsNavAction
import com.velosobr.piuds.components.kids.PiuKidsNavBar
import com.velosobr.piuds.components.kids.PiuPipo
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h900dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PiuKidsNavBarLayoutTest {
    @Test
    fun `com fonte 2_0 o botao do fim fica em ate 96 dp`() =
        runComposeUiTest {
            setContent {
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                    PiuTheme(journey = Journey.KIDS) {
                        Box(Modifier.width(360.dp)) {
                            PiuKidsNavBar(
                                listOf(PiuNavItem(PiuPipo, "Explorar"), PiuNavItem(PiuPipo, "Álbum")),
                                selected = 0,
                                onSelect = {},
                                action = PiuKidsNavAction(PiuPipo, "Para adultos", onClick = {}),
                            )
                        }
                    }
                }
            }
            assertThat(onNodeWithText("Para adultos").getBoundsInRoot().width).isLessThanOrEqualTo(96.dp)
        }
}
