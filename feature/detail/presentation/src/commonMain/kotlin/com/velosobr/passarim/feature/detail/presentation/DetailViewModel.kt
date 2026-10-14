package com.velosobr.passarim.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.core.domain.onFailure
import com.velosobr.passarim.core.domain.onSuccess
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.core.presentation.toScreenError
import com.velosobr.passarim.feature.detail.domain.AudioPlayer
import com.velosobr.passarim.feature.detail.domain.DetailRepository
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail
import com.velosobr.passarim.feature.detail.domain.isSafeLink
import com.velosobr.passarim.feature.detail.domain.isValidSpeciesId
import com.velosobr.passarim.feature.detail.domain.toSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val KEY_SPECIES_ID = "id"

class DetailViewModel(
    savedState: SavedStateHandle,
    private val repository: DetailRepository,
    private val favorites: FavoritesRepository,
    private val settings: SettingsRepository,
    private val player: AudioPlayer,
) : ViewModel() {
    private val id: String = savedState[KEY_SPECIES_ID] ?: ""

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val eventChannel = Channel<DetailEvent>(Channel.BUFFERED)
    val events: Flow<DetailEvent> = eventChannel.receiveAsFlow()

    private var retryJob: Job? = null

    private val detail: SpeciesDetail?
        get() = (_state.value.phase as? DetailPhase.Content)?.detail

    init {
        favorites.observeIds().onEach { ids -> _state.update { it.copy(isFavorite = id in ids) } }.launchIn(viewModelScope)
        player.state.onEach { playback -> _state.update { it.copy(player = playback) } }.launchIn(viewModelScope)
        if (isValidSpeciesId(id)) load() else _state.update { it.copy(phase = DetailPhase.NotFound) }
    }

    fun onAction(action: DetailAction) {
        when (action) {
            DetailAction.Back -> eventChannel.trySend(DetailEvent.NavigateBack)
            DetailAction.Retry -> if (_state.value.retryEnabled) load()
            DetailAction.ToggleFavorite -> detail?.let { viewModelScope.launch { favorites.toggle(it.toSummary()) } }
            DetailAction.TogglePlay -> togglePlay()
            is DetailAction.Seek -> player.seekTo(action.positionMs)
            DetailAction.ToggleCredits -> _state.update { it.copy(creditsExpanded = !it.creditsExpanded) }
            is DetailAction.OpenCredit -> if (isSafeLink(action.url)) eventChannel.trySend(DetailEvent.OpenUrl(action.url))
            DetailAction.ScreenPaused -> if (isPlaying()) player.pause()
        }
    }

    override fun onCleared() {
        player.release()
    }

    private fun load() {
        retryJob?.cancel()
        _state.update { it.copy(phase = DetailPhase.Loading, retryEnabled = true) }
        viewModelScope.launch {
            repository
                .get(id)
                .onSuccess { detail ->
                    _state.update { it.copy(phase = DetailPhase.Content(detail)) }
                    if (id in favorites.observeIds().first()) favorites.refresh(detail.toSummary())
                    if (settings.observe().first().autoPlay) detail.audio?.let { player.play(it.url) }
                }.onFailure { error ->
                    if (error == DataError.Network.NotFound) {
                        _state.update { it.copy(phase = DetailPhase.NotFound) }
                    } else {
                        showError(error.toScreenError())
                    }
                }
        }
    }

    private fun showError(error: ScreenError) {
        val wait = (error as? ScreenError.RateLimited)?.retryAfterSeconds
        _state.update { it.copy(phase = DetailPhase.Error(error), retryEnabled = wait == null) }
        if (wait != null) {
            retryJob =
                viewModelScope.launch {
                    delay(wait * 1_000L)
                    _state.update { it.copy(retryEnabled = true) }
                }
        }
    }

    private fun togglePlay() {
        val audio = detail?.audio ?: return
        if (isPlaying()) player.pause() else player.play(audio.url)
    }

    private fun isPlaying(): Boolean = _state.value.player.let { it is PlaybackState.Playing || it == PlaybackState.Loading }
}
