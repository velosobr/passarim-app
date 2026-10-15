package com.velosobr.passarim.feature.favorites.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesRoot(
    onOpenDetail: (String) -> Unit,
    onOpenExplore: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FavoritesEvent.NavigateToDetail -> onOpenDetail(event.id)
            FavoritesEvent.NavigateToExplore -> onOpenExplore()
        }
    }
    FavoritesScreen(state = state, onAction = viewModel::onAction)
}
