package com.velosobr.passarim.app.debug

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.StateFilter
import com.velosobr.passarim.core.presentation.UiText
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
class DebugViewModelTest {
    private class FakeFilters(
        var next: Result<Filters, DataError.Network>,
    ) : FiltersRepository {
        var calls = 0

        /** Permite segurar a resposta para observar o estado "carregando". */
        var gate: CompletableDeferred<Unit>? = null

        override suspend fun get(): Result<Filters, DataError.Network> {
            calls++
            gate?.await()
            return next
        }
    }

    private val filters =
        Filters(
            biomes = listOf(BiomeFilter("cerrado", "Cerrado", 26), BiomeFilter("pampa", "Pampa", 15)),
            states = listOf(StateFilter("SP", 29)),
        )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `ao abrir carrega os filtros e mostra as contagens`() {
        val vm = DebugViewModel(FakeFilters(Result.Success(filters)))
        assertThat(vm.state.value).isEqualTo(DebugState(isLoading = false, biomeCount = 2, stateCount = 1))
    }

    @Test
    fun `falha de rede mostra o erro e nenhuma contagem`() {
        val vm = DebugViewModel(FakeFilters(Result.Failure(DataError.Network.NoConnection)))
        val s = vm.state.value
        assertThat(s.isLoading).isEqualTo(false)
        assertThat(s.biomeCount).isNull()
        assertThat(s.error).isNotNull().isInstanceOf<UiText.Resource>()
    }

    @Test
    fun `tentar de novo recarrega e limpa o erro`() {
        val repo = FakeFilters(Result.Failure(DataError.Network.Timeout))
        val vm = DebugViewModel(repo)
        assertThat(vm.state.value.error).isNotNull()

        repo.next = Result.Success(filters)
        vm.onAction(DebugAction.Retry)

        assertThat(vm.state.value).isEqualTo(DebugState(isLoading = false, biomeCount = 2, stateCount = 1))
        assertThat(repo.calls).isEqualTo(2)
    }

    @Test
    fun `enquanto a chamada nao termina o estado e carregando`() =
        runTest {
            val repo = FakeFilters(Result.Success(filters)).also { it.gate = CompletableDeferred() }
            val vm = DebugViewModel(repo)
            vm.state.test {
                assertThat(awaitItem().isLoading).isEqualTo(true)
                repo.gate!!.complete(Unit)
                assertThat(awaitItem()).isEqualTo(DebugState(isLoading = false, biomeCount = 2, stateCount = 1))
            }
        }
}
