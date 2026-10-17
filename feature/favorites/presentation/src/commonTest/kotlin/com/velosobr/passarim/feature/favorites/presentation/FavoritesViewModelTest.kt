// Testes do ViewModel dos Favoritos com um repositório falso em memória.

package com.velosobr.passarim.feature.favorites.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

// Duas aves de exemplo.
private val arara = SpeciesSummary("anodorhynchus-hyacinthinus", "Arara-azul-grande", "Anodorhynchus hyacinthinus", null)
private val tucano = SpeciesSummary("ramphastos-toco", "Tucano-toco", "Ramphastos toco", "https://media/tucano.webp")

/**
 * Favoritos falsos num StateFlow. Começar com null simula o banco ainda sem
 * resposta (filterNotNull só deixa passar a lista quando ela chega).
 */
private class FakeFavoritesRepository(
    initial: List<SpeciesSummary>?,
) : FavoritesRepository {
    val favorites = MutableStateFlow(initial)
    val toggled = mutableListOf<SpeciesSummary>()

    override fun observeIds(): Flow<Set<String>> = observeAll().map { list -> list.map { it.id }.toSet() }

    override fun observeAll(): Flow<List<SpeciesSummary>> = favorites.filterNotNull()

    override suspend fun toggle(summary: SpeciesSummary) {
        toggled += summary
        favorites.value = favorites.value.orEmpty().filterNot { it.id == summary.id }
    }

    override suspend fun refresh(summary: SpeciesSummary) = Unit
}

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `carregando ate o banco responder`() {
        val vm = FavoritesViewModel(FakeFavoritesRepository(initial = null))

        assertThat(vm.state.value).isEqualTo(FavoritesState.Loading)
    }

    @Test
    fun `sem favoritos fica vazio`() {
        val vm = FavoritesViewModel(FakeFavoritesRepository(initial = emptyList()))

        assertThat(vm.state.value).isEqualTo(FavoritesState.Empty)
    }

    @Test
    fun `com favoritos mostra a lista na ordem do repositorio`() {
        val vm = FavoritesViewModel(FakeFavoritesRepository(initial = listOf(tucano, arara)))

        assertThat(vm.state.value).isEqualTo(FavoritesState.Content(listOf(tucano, arara)))
    }

    @Test
    fun `desfavoritar remove da lista e o ultimo leva ao vazio`() {
        val repository = FakeFavoritesRepository(initial = listOf(tucano, arara))
        val vm = FavoritesViewModel(repository)

        vm.onAction(FavoritesAction.ToggleFavorite(tucano))
        assertThat(vm.state.value).isEqualTo(FavoritesState.Content(listOf(arara)))

        vm.onAction(FavoritesAction.ToggleFavorite(arara))
        assertThat(vm.state.value).isEqualTo(FavoritesState.Empty)
        assertThat(repository.toggled).containsExactly(tucano, arara)
    }

    @Test
    fun `abrir uma ave e explorar viram eventos de navegacao`() =
        runTest {
            val vm = FavoritesViewModel(FakeFavoritesRepository(initial = listOf(arara)))

            vm.events.test {
                vm.onAction(FavoritesAction.OpenDetail(arara.id))
                assertThat(awaitItem()).isEqualTo(FavoritesEvent.NavigateToDetail(arara.id))
                vm.onAction(FavoritesAction.ExploreClick)
                assertThat(awaitItem()).isEqualTo(FavoritesEvent.NavigateToExplore)
            }
        }
}
