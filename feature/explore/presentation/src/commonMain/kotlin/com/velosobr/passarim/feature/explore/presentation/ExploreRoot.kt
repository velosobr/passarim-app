package com.velosobr.passarim.feature.explore.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

/** Liga a tela ao ViewModel (Koin) e transforma os eventos em navegação, que fica a cargo do `app`. */
@Composable
fun ExploreRoot(
    onOpenDetail: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    viewModel: ExploreViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ExploreEvent.NavigateToDetail -> onOpenDetail(event.id)
            ExploreEvent.NavigateToFavorites -> onOpenFavorites()
        }
    }
    ExploreScreen(state = state, onAction = viewModel::onAction)
}
