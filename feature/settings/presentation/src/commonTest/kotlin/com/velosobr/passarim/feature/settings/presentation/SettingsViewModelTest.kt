// Testes do ViewModel da aba Configurações.

package com.velosobr.passarim.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val repository = FakeSettingsRepository(Settings(theme = ThemeMode.DARK))

    /** viewModel cria o ViewModel com ou sem suporte a cores dinâmicas. */
    private fun viewModel(dynamicColorsSupported: Boolean = true) =
        SettingsViewModel(repository, dynamicColorsSupported = dynamicColorsSupported, versionName = "1.0.0")

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `mostra as configuracoes gravadas e a versao`() {
        val state = viewModel().state.value

        assertThat(state.settings.theme).isEqualTo(ThemeMode.DARK)
        assertThat(state.versionName).isEqualTo("1.0.0")
    }

    @Test
    fun `escolher o tema grava na hora`() {
        val vm = viewModel()

        vm.onAction(SettingsAction.SelectTheme(ThemeMode.LIGHT))

        assertThat(repository.settings.value.theme).isEqualTo(ThemeMode.LIGHT)
        assertThat(vm.state.value.settings.theme).isEqualTo(ThemeMode.LIGHT)
    }

    @Test
    fun `cores dinamicas e autoplay gravam o valor do interruptor`() {
        val vm = viewModel()

        vm.onAction(SettingsAction.ChangeDynamicColors(true))
        vm.onAction(SettingsAction.ChangeAutoPlay(true))

        assertThat(repository.settings.value.dynamicColors).isTrue()
        assertThat(repository.settings.value.autoPlay).isTrue()
    }

    @Test
    fun `cores dinamicas so aparecem onde ha suporte`() {
        assertThat(viewModel(dynamicColorsSupported = true).state.value.dynamicColorsSupported).isTrue()
        assertThat(viewModel(dynamicColorsSupported = false).state.value.dynamicColorsSupported).isFalse()
    }

    @Test
    fun `dados e armazenamento e sobre viram eventos de navegacao`() =
        runTest {
            val vm = viewModel()

            vm.events.test {
                vm.onAction(SettingsAction.OpenStorage)
                assertThat(awaitItem()).isEqualTo(SettingsEvent.NavigateToStorage)
                vm.onAction(SettingsAction.OpenAbout)
                assertThat(awaitItem()).isEqualTo(SettingsEvent.NavigateToAbout)
            }
        }
}
