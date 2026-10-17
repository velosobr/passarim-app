// A ligação entre a aba Favoritos e o ViewModel.

package com.velosobr.passarim.feature.favorites.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

/** Liga a tela ao ViewModel e transforma os eventos em navegação, que fica a cargo do app. */
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
