// Testes do ViewModel da Escolha: os quatro estados do cartão Kids, o
// "Tentar de novo" e a escolha, que grava a jornada.

package com.velosobr.passarim.feature.journey.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JourneyChoiceViewModelTest {
    private val settings = FakeSettingsRepository()
    private val kids = FakeKidsAvailabilityRepository()

    private fun viewModel() = JourneyChoiceViewModel(settings, kids)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `enquanto confere o cartao Kids fica conferindo`() {
        kids.gate = CompletableDeferred()
        val vm = viewModel()
        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.CHECKING)
        kids.gate?.complete(Unit)
        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.AVAILABLE)
    }

    @Test
    fun `BFF com o Kids deixa o cartao disponivel`() {
        assertThat(viewModel().state.value.kids).isEqualTo(KidsAvailabilityState.AVAILABLE)
    }

    @Test
    fun `sem rede o cartao fica sem rede`() {
        kids.result = Result.Failure(DataError.Network.NoConnection)
        assertThat(viewModel().state.value.kids).isEqualTo(KidsAvailabilityState.OFFLINE)
    }

    @Test
    fun `BFF sem o Kids esconde o cartao`() {
        kids.result = Result.Success(false)
        assertThat(viewModel().state.value.kids).isEqualTo(KidsAvailabilityState.HIDDEN)
    }

    @Test
    fun `tentar de novo confere outra vez e passa por conferindo`() {
        kids.result = Result.Failure(DataError.Network.Timeout)
        val vm = viewModel()
        kids.result = Result.Success(true)
        kids.gate = CompletableDeferred()

        vm.onAction(JourneyChoiceAction.RetryKids)
        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.CHECKING)
        kids.gate?.complete(Unit)

        assertThat(vm.state.value.kids).isEqualTo(KidsAvailabilityState.AVAILABLE)
        assertThat(kids.calls).isEqualTo(2)
    }

    @Test
    fun `escolher o adulto grava ADULT mesmo sem o Kids`() {
        kids.result = Result.Success(false)
        viewModel().onAction(JourneyChoiceAction.ChooseJourney(Journey.ADULT))
        assertThat(settings.settings.value.journey).isEqualTo(Journey.ADULT)
    }

    @Test
    fun `escolher o Kids disponivel grava KIDS`() {
        viewModel().onAction(JourneyChoiceAction.ChooseJourney(Journey.KIDS))
        assertThat(settings.settings.value.journey).isEqualTo(Journey.KIDS)
    }

    @Test
    fun `o Kids indisponivel nao grava nada`() {
        kids.result = Result.Failure(DataError.Network.NoConnection)
        viewModel().onAction(JourneyChoiceAction.ChooseJourney(Journey.KIDS))
        assertThat(settings.settings.value.journey).isNull()
        assertThat(settings.journeyWrites).isEqualTo(0)
    }
}
