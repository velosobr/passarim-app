package com.velosobr.passarim.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.domain.toSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val repo = FakeDetailRepository()
    private val favorites = FakeFavoritesRepository()
    private val player = FakeAudioPlayer()

    private fun viewModel(
        id: String = ARARA_ID,
        autoPlay: Boolean = false,
    ) = DetailViewModel(SavedStateHandle(mapOf(KEY_SPECIES_ID to id)), repo, favorites, FakeSettingsRepository(autoPlay), player)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `ao abrir carrega a especie do id da rota`() {
        val vm = viewModel()
        assertThat(repo.calls).containsExactly(ARARA_ID)
        assertThat(vm.state.value.phase).isEqualTo(DetailPhase.Content(arara))
    }

    @Test
    fun `id invalido vira nao encontrada sem chamar o repositorio`() {
        val vm = viewModel(id = "../admin")
        assertThat(repo.calls).isEmpty()
        assertThat(vm.state.value.phase).isEqualTo(DetailPhase.NotFound)
    }

    @Test
    fun `especie inexistente vira nao encontrada`() {
        repo.next = Result.Failure(DataError.Network.NotFound)
        assertThat(viewModel().state.value.phase).isEqualTo(DetailPhase.NotFound)
    }

    @Test
    fun `cada erro de rede vira o erro de tela correspondente`() {
        mapOf(
            DataError.Network.NoConnection to ScreenError.NoConnection,
            DataError.Network.Timeout to ScreenError.NoConnection,
            DataError.Network.ServiceUnavailable to ScreenError.ServiceUnavailable,
            DataError.Network.Internal to ScreenError.ServiceUnavailable,
            DataError.Network.InvalidParameter to ScreenError.Generic,
            DataError.Network.Unknown to ScreenError.Generic,
        ).forEach { (error, expected) ->
            repo.next = Result.Failure(error)
            assertThat(viewModel().state.value.phase).isEqualTo(DetailPhase.Error(expected))
        }
    }

    @Test
    fun `RateLimited so libera tentar de novo depois do Retry-After`() =
        runTest {
            repo.next = Result.Failure(DataError.Network.RateLimited(30))
            val vm = viewModel()
            assertThat(vm.state.value.retryEnabled).isFalse()

            vm.onAction(DetailAction.Retry)
            assertThat(repo.calls).containsExactly(ARARA_ID)

            advanceTimeBy(30_001)
            assertThat(vm.state.value.retryEnabled).isTrue()
        }

    @Test
    fun `tentar de novo recarrega a especie`() {
        repo.next = Result.Failure(DataError.Network.NoConnection)
        val vm = viewModel()
        repo.next = Result.Success(arara)

        vm.onAction(DetailAction.Retry)

        assertThat(repo.calls).containsExactly(ARARA_ID, ARARA_ID)
        assertThat(vm.state.value.phase).isEqualTo(DetailPhase.Content(arara))
    }

    @Test
    fun `favoritar envia o resumo da especie e o estado acompanha os favoritos`() {
        val vm = viewModel()
        assertThat(vm.state.value.isFavorite).isFalse()

        vm.onAction(DetailAction.ToggleFavorite)

        assertThat(favorites.toggled).containsExactly(arara.toSummary())
        assertThat(vm.state.value.isFavorite).isTrue()
    }

    @Test
    fun `abrir uma especie ja favorita atualiza o resumo guardado`() {
        favorites.ids.value = setOf(ARARA_ID)
        viewModel()
        assertThat(favorites.refreshed).containsExactly(arara.toSummary())
    }

    @Test
    fun `abrir uma especie que nao e favorita nao mexe nos favoritos`() {
        viewModel()
        assertThat(favorites.refreshed).isEmpty()
    }

    @Test
    fun `tocar pausar e retomar o canto`() {
        val vm = viewModel()

        vm.onAction(DetailAction.TogglePlay)
        player.state.value = PlaybackState.Playing(1_200)
        vm.onAction(DetailAction.TogglePlay)
        player.state.value = PlaybackState.Paused(1_200)
        vm.onAction(DetailAction.TogglePlay)

        assertThat(player.calls).containsExactly("play:$AUDIO_URL", "pause", "play:$AUDIO_URL")
    }

    @Test
    fun `o estado do player aparece no estado da tela`() {
        val vm = viewModel()
        player.state.value = PlaybackState.Playing(2_500)
        assertThat(vm.state.value.player).isEqualTo(PlaybackState.Playing(2_500))
    }

    @Test
    fun `seek move o player`() {
        val vm = viewModel()
        vm.onAction(DetailAction.Seek(3_000))
        assertThat(player.calls).containsExactly("seek:3000")
    }

    @Test
    fun `sem audio tocar nao faz nada`() {
        repo.next = Result.Success(arara.copy(audio = null))
        val vm = viewModel()
        vm.onAction(DetailAction.TogglePlay)
        assertThat(player.calls).isEmpty()
    }

    @Test
    fun `depois de um erro do player tocar tenta de novo`() {
        val vm = viewModel()
        player.state.value = PlaybackState.Error
        assertThat(vm.state.value.player).isEqualTo(PlaybackState.Error)

        vm.onAction(DetailAction.TogglePlay)

        assertThat(player.calls).containsExactly("play:$AUDIO_URL")
    }

    @Test
    fun `perder o foco pausa o player`() {
        val vm = viewModel()
        player.state.value = PlaybackState.Playing(100)
        vm.onAction(DetailAction.ScreenPaused)
        assertThat(player.calls).containsExactly("pause")
    }

    @Test
    fun `sair da tela libera o player`() {
        val store = ViewModelStore()
        ViewModelProvider.create(store, viewModelFactory { initializer { viewModel() } })[DetailViewModel::class]

        store.clear()

        assertThat(player.calls).containsExactly("release")
    }

    @Test
    fun `com reproducao automatica ligada toca ao carregar`() {
        viewModel(autoPlay = true)
        assertThat(player.calls).containsExactly("play:$AUDIO_URL")
    }

    @Test
    fun `com reproducao automatica desligada nao toca`() {
        viewModel(autoPlay = false)
        assertThat(player.calls).isEmpty()
    }

    @Test
    fun `reproducao automatica sem audio nao toca`() {
        repo.next = Result.Success(arara.copy(audio = null))
        viewModel(autoPlay = true)
        assertThat(player.calls).isEmpty()
    }

    @Test
    fun `a linha de creditos expande e recolhe`() {
        val vm = viewModel()
        vm.onAction(DetailAction.ToggleCredits)
        assertThat(vm.state.value.creditsExpanded).isTrue()
        vm.onAction(DetailAction.ToggleCredits)
        assertThat(vm.state.value.creditsExpanded).isFalse()
    }

    @Test
    fun `so links https de credito abrem`() =
        runTest {
            val vm = viewModel()
            vm.events.test {
                vm.onAction(DetailAction.OpenCredit("javascript:alert(1)"))
                vm.onAction(DetailAction.OpenCredit("http://www.inaturalist.org/observations/1"))
                vm.onAction(DetailAction.OpenCredit(""))
                vm.onAction(DetailAction.OpenCredit("https://xeno-canto.org/403887"))
                assertThat(awaitItem()).isEqualTo(DetailEvent.OpenUrl("https://xeno-canto.org/403887"))
            }
        }

    @Test
    fun `voltar emite a navegacao de volta`() =
        runTest {
            val vm = viewModel()
            vm.events.test {
                vm.onAction(DetailAction.Back)
                assertThat(awaitItem()).isEqualTo(DetailEvent.NavigateBack)
            }
        }
}
