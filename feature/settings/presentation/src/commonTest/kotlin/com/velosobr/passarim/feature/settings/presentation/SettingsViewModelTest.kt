// Testes do ViewModel da aba Configurações.

package com.velosobr.passarim.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import kotlinx.coroutines.CompletableDeferred
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
    private val repository = FakeSettingsRepository(Settings(theme = ThemeMode.DARK, autoPlay = true, journey = Journey.ADULT))
    private val kids = FakeKidsAvailabilityRepository()

    /** viewModel cria o ViewModel com ou sem suporte a cores dinâmicas. */
    private fun viewModel(dynamicColorsSupported: Boolean = true) =
        SettingsViewModel(repository, kids, dynamicColorsSupported = dynamicColorsSupported, versionName = "1.0.0")

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

    @Test
    fun `o item Kids fica conferindo ate o BFF responder`() {
        kids.gate = CompletableDeferred()
        val vm = viewModel()
        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.CHECKING)
        kids.gate?.complete(Unit)
        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.AVAILABLE)
    }

    @Test
    fun `o item Kids segue a checagem do BFF`() {
        kids.result = Result.Success(false)
        assertThat(viewModel().state.value.kids).isEqualTo(KidsAvailabilityState.HIDDEN)
        kids.result = Result.Failure(DataError.Network.NoConnection)
        assertThat(viewModel().state.value.kids).isEqualTo(KidsAvailabilityState.OFFLINE)
    }

    @Test
    fun `o item Kids disponivel grava KIDS na hora sem mexer no tema nem na reproducao`() {
        viewModel().onAction(SettingsAction.OpenKids)

        assertThat(repository.settings.value).isEqualTo(Settings(theme = ThemeMode.DARK, autoPlay = true, journey = Journey.KIDS))
    }

    @Test
    fun `sem rede o toque no item Kids confere de novo e nao grava`() {
        kids.result = Result.Failure(DataError.Network.NoConnection)
        val vm = viewModel()
        kids.result = Result.Success(true)

        vm.onAction(SettingsAction.OpenKids)

        assertThat(kids.calls).isEqualTo(2)
        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.AVAILABLE)
        assertThat(repository.settings.value.journey).isEqualTo(Journey.ADULT)
    }

    @Test
    fun `conferindo o toque no item Kids nao faz nada`() {
        kids.gate = CompletableDeferred()
        val vm = viewModel()

        vm.onAction(SettingsAction.OpenKids)

        assertThat(kids.calls).isEqualTo(1)
        assertThat(repository.settings.value.journey).isEqualTo(Journey.ADULT)
    }
}
