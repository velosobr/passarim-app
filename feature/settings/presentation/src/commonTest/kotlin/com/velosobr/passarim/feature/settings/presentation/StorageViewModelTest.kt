package com.velosobr.passarim.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
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
class StorageViewModelTest {
    private val cleaner = FakeImageCacheCleaner()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `limpar cache chama o limpador e confirma na tela`() {
        val gate = CompletableDeferred<Unit>()
        cleaner.gate = gate
        val vm = StorageViewModel(cleaner)

        vm.onAction(StorageAction.ClearCache)
        assertThat(vm.state.value).isEqualTo(StorageState.Clearing)

        gate.complete(Unit)
        assertThat(vm.state.value).isEqualTo(StorageState.Cleared)
        assertThat(cleaner.calls).isEqualTo(1)
    }

    @Test
    fun `limpar enquanto limpa nao chama o limpador de novo`() {
        cleaner.gate = CompletableDeferred()
        val vm = StorageViewModel(cleaner)

        vm.onAction(StorageAction.ClearCache)
        vm.onAction(StorageAction.ClearCache)

        assertThat(cleaner.calls).isEqualTo(1)
    }

    @Test
    fun `voltar vira evento`() =
        runTest {
            val vm = StorageViewModel(cleaner)

            vm.events.test {
                vm.onAction(StorageAction.Back)
                assertThat(awaitItem()).isEqualTo(StorageEvent.NavigateBack)
            }
        }
}
