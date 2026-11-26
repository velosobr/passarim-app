// Testes do ViewModel da trava para adultos: o teclado, o acerto (grava
// ADULT), o erro (mensagem e conta nova) e o voltar (não grava nada).

package com.velosobr.passarim.feature.journey.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KidsGateViewModelTest {
    private val repository = FakeSettingsRepository(Settings(journey = Journey.KIDS))
    private val viewModel by lazy { KidsGateViewModel(repository, Random(3)) }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Digita [text] algarismo por algarismo, como no teclado da tela. */
    private fun type(text: String) = text.forEach { viewModel.onAction(KidsGateAction.TypeDigit(it.digitToInt())) }

    @Test
    fun `abre com uma conta sem algarismos e sem erro`() {
        val state = viewModel.state.value
        assertThat(state.answer).isEqualTo("")
        assertThat(state.wrongAnswer).isFalse()
        assertThat(state.question.first in 13..99).isTrue()
    }

    @Test
    fun `o teclado aceita no maximo 3 algarismos`() {
        type("1234")
        assertThat(viewModel.state.value.answer).isEqualTo("123")
    }

    @Test
    fun `apagar remove o ultimo algarismo e nao faz nada sem algarismos`() {
        type("45")
        viewModel.onAction(KidsGateAction.Erase)
        assertThat(viewModel.state.value.answer).isEqualTo("4")
        viewModel.onAction(KidsGateAction.Erase)
        viewModel.onAction(KidsGateAction.Erase)
        assertThat(viewModel.state.value.answer).isEqualTo("")
    }

    @Test
    fun `acertar grava a jornada do adulto`() {
        type(
            viewModel.state.value.question.answer
                .toString(),
        )
        viewModel.onAction(KidsGateAction.Confirm)
        assertThat(repository.settings.value.journey).isEqualTo(Journey.ADULT)
    }

    @Test
    fun `errar mostra a mensagem e sorteia outra conta sem gravar nada`() {
        val before = viewModel.state.value.question
        type((before.answer + 1).toString())
        viewModel.onAction(KidsGateAction.Confirm)

        val after = viewModel.state.value
        assertThat(after.wrongAnswer).isTrue()
        assertThat(after.question).isNotEqualTo(before)
        assertThat(after.answer).isEqualTo("")
        assertThat(repository.journeyWrites).isEqualTo(0)
    }

    @Test
    fun `sem limite de tentativas o acerto depois de errar ainda vale`() {
        repeat(5) {
            type("1")
            viewModel.onAction(KidsGateAction.Confirm)
        }
        type(
            viewModel.state.value.question.answer
                .toString(),
        )
        viewModel.onAction(KidsGateAction.Confirm)
        assertThat(repository.settings.value.journey).isEqualTo(Journey.ADULT)
    }

    @Test
    fun `confirmar sem algarismos nao conta como erro`() {
        val before = viewModel.state.value.question
        viewModel.onAction(KidsGateAction.Confirm)
        assertThat(viewModel.state.value.wrongAnswer).isFalse()
        assertThat(viewModel.state.value.question).isEqualTo(before)
    }

    @Test
    fun `voltar vira evento de navegacao e nao grava nada`() =
        runTest {
            viewModel.events.test {
                viewModel.onAction(KidsGateAction.Back)
                assertThat(awaitItem()).isEqualTo(KidsGateEvent.NavigateBack)
            }
            assertThat(repository.journeyWrites).isEqualTo(0)
            assertThat(repository.settings.value.journey).isEqualTo(Journey.KIDS)
        }
}
