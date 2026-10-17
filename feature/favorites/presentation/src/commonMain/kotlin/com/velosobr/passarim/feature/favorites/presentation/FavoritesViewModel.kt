package com.velosobr.passarim.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velosobr.passarim.core.domain.FavoritesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favorites: FavoritesRepository,
) : ViewModel() {
    val state: StateFlow<FavoritesState> =
        favorites
            .observeAll()
            .map { items -> if (items.isEmpty()) FavoritesState.Empty else FavoritesState.Content(items) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesState.Loading)

    private val eventChannel = Channel<FavoritesEvent>(Channel.BUFFERED)
    val events: Flow<FavoritesEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.OpenDetail -> eventChannel.trySend(FavoritesEvent.NavigateToDetail(action.id))
            is FavoritesAction.ToggleFavorite -> viewModelScope.launch { favorites.toggle(action.summary) }
            FavoritesAction.ExploreClick -> eventChannel.trySend(FavoritesEvent.NavigateToExplore)
        }
    }
}
