// A ligação entre a tela de Detalhe e o ViewModel.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

/**
 * Liga a tela ao ViewModel: avisa quando ela sai de primeiro plano
 * (LifecycleEventEffect com ON_PAUSE: app em segundo plano ou outra tela por
 * cima), para o canto parar, e transforma os
 * eventos em navegação e em abertura de links (LocalUriHandler abre a URL no
 * navegador do sistema).
 */
@Composable
fun DetailRoot(
    onBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { viewModel.onAction(DetailAction.ScreenPaused) }
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            DetailEvent.NavigateBack -> onBack()
            is DetailEvent.OpenUrl -> uriHandler.openUri(event.url)
        }
    }
    DetailScreen(state = state, onAction = viewModel::onAction)
}
