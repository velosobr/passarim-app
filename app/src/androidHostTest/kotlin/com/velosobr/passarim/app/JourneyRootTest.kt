// Testes da raiz do app por jornada: qual tela abre para cada jornada gravada,
// o tema de cada uma e a cor dos ícones das barras do sistema. As telas de
// verdade (que pedem o Koin e a rede) entram como textos simples. Rodam no
// Robolectric, numa Activity de verdade (runAndroidComposeUiTest), para ler a
// janela.

package com.velosobr.passarim.app

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import androidx.core.view.WindowCompat
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class JourneyRootTest {
    /** O isDark que o PiuTheme expôs para a tela, guardado pelo teste. */
    private var screenIsDark: Boolean? = null

    private fun runRoot(
        settings: Settings,
        check: AndroidComposeUiTest<ComponentActivity>.() -> Unit,
    ) = runAndroidComposeUiTest<ComponentActivity> {
        setContent {
            JourneyRoot(
                settings = settings,
                choice = { Text("Escolha") },
                adult = { Text("Adulto").also { screenIsDark = PiuTheme.isDark } },
                kids = { Text("Kids").also { screenIsDark = PiuTheme.isDark } },
            )
        }
        check()
    }

    /** Se os ícones das barras do sistema estão escuros (para um fundo claro). */
    private fun AndroidComposeUiTest<ComponentActivity>.lightBars(): Boolean {
        val window = checkNotNull(activity).window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        assertThat(controller.isAppearanceLightNavigationBars).isEqualTo(controller.isAppearanceLightStatusBars)
        return controller.isAppearanceLightStatusBars
    }

    @Test
    fun `sem jornada gravada abre a Escolha`() =
        runRoot(Settings(journey = null)) {
            onNodeWithText("Escolha").assertExists()
            onNodeWithText("Adulto").assertDoesNotExist()
            onNodeWithText("Kids").assertDoesNotExist()
        }

    @Test
    fun `com ADULT abre o adulto`() =
        runRoot(Settings(journey = Journey.ADULT)) {
            onNodeWithText("Adulto").assertExists()
            onNodeWithText("Escolha").assertDoesNotExist()
        }

    @Test
    fun `com KIDS abre o Kids`() =
        runRoot(Settings(journey = Journey.KIDS)) {
            onNodeWithText("Kids").assertExists()
            onNodeWithText("Adulto").assertDoesNotExist()
        }

    @Test
    fun `o adulto no tema escuro gravado fica escuro com os icones das barras claros`() =
        runRoot(Settings(theme = ThemeMode.DARK, journey = Journey.ADULT)) {
            waitForIdle()
            assertThat(screenIsDark).isEqualTo(true)
            assertThat(lightBars()).isFalse()
        }

    @Test
    fun `o Kids com o tema escuro gravado continua claro com os icones das barras escuros`() =
        runRoot(Settings(theme = ThemeMode.DARK, dynamicColors = true, journey = Journey.KIDS)) {
            waitForIdle()
            assertThat(screenIsDark).isEqualTo(false)
            assertThat(lightBars()).isTrue()
        }
}
