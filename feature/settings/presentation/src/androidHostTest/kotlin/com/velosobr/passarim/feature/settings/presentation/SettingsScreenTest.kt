// Testes da aba Configurações em grupos de cartões (frente E3): as seções como
// cabeçalhos, o tema como botões segmentados que o leitor de tela anuncia como
// opções marcáveis, as chaves e as linhas que abrem outra tela. Rodam no
// Robolectric com runComposeUiTest (veja FavoritesScreenTest).

package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
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
class SettingsScreenTest {
    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    /** Confere o papel que o leitor de tela anuncia ("botão de opção", "chave"). */
    private fun hasRole(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private val state =
        SettingsState(
            Settings(theme = ThemeMode.LIGHT, dynamicColors = false, autoPlay = true),
            dynamicColorsSupported = true,
            versionName = "1.0.0",
        )

    @Test
    fun `as quatro secoes sao cabecalhos e nao ha a secao para criancas`() =
        runComposeUiTest {
            setContent { PiuTheme { SettingsScreen(state, onAction = {}) } }
            listOf("Configurações", "Aparência", "Reprodução", "Dados", "Sobre").forEach { title ->
                onNode(hasText(title) and isHeading).performScrollTo()
            }
            onNodeWithText("Para crianças").assertDoesNotExist()
        }

    @Test
    fun `o tema e um grupo de botoes segmentados com a opcao atual marcada`() =
        runComposeUiTest {
            val actions = mutableListOf<SettingsAction>()
            setContent { PiuTheme { SettingsScreen(state, onAction = { actions += it }) } }
            onNodeWithText("Claro").assert(hasRole(Role.RadioButton)).assertIsSelected().assertHeightIsAtLeast(48.dp)
            onNodeWithText("Sistema").assertIsNotSelected()
            onNodeWithText("Escuro").assert(hasRole(Role.RadioButton)).assertIsNotSelected().performClick()
            assertThat(actions).containsExactly(SettingsAction.SelectTheme(ThemeMode.DARK))
            // Segmentados: as três opções lado a lado, na mesma linha.
            val tops = listOf("Sistema", "Claro", "Escuro").map { onNodeWithText(it).getBoundsInRoot().top }
            assertThat(tops.distinct()).hasSize(1)
        }

    @Test
    fun `com fonte grande as opcoes do tema ficam uma embaixo da outra`() =
        runComposeUiTest {
            setContent {
                // Density com fontScale 2: o mesmo que a pessoa aumentar a fonte do aparelho para 200%.
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                    PiuTheme { SettingsScreen(state, onAction = {}) }
                }
            }
            val tops = listOf("Sistema", "Claro", "Escuro").map { onNodeWithText(it).getBoundsInRoot().top }
            assertThat(tops).isEqualTo(tops.sorted())
            assertThat(tops.distinct()).hasSize(3)
        }

    @Test
    fun `as chaves de cores e de reproducao mudam a preferencia`() =
        runComposeUiTest {
            val actions = mutableListOf<SettingsAction>()
            setContent { PiuTheme { SettingsScreen(state, onAction = { actions += it }) } }
            onNodeWithText("Cores do papel de parede").assertIsOff().performClick()
            onNodeWithText("Tocar o canto ao abrir")
                .performScrollTo()
                .assert(hasRole(Role.Switch))
                .assertIsOn()
                .performClick()
            assertThat(actions).containsExactly(SettingsAction.ChangeDynamicColors(true), SettingsAction.ChangeAutoPlay(false))
        }

    @Test
    fun `sem cores dinamicas no sistema a chave nao aparece`() =
        runComposeUiTest {
            setContent { PiuTheme { SettingsScreen(state.copy(dynamicColorsSupported = false), onAction = {}) } }
            onNodeWithText("Cores do papel de parede").assertDoesNotExist()
        }

    @Test
    fun `armazenamento e sobre abrem as subtelas`() =
        runComposeUiTest {
            val actions = mutableListOf<SettingsAction>()
            setContent { PiuTheme { SettingsScreen(state, onAction = { actions += it }) } }
            onNodeWithText("Armazenamento").performScrollTo().performClick()
            onNodeWithText("Versão 1.0.0, fontes e licenças").performScrollTo()
            onNodeWithText("Sobre o Passarim").performClick()
            assertThat(actions).containsExactly(SettingsAction.OpenStorage, SettingsAction.OpenAbout)
        }
}
