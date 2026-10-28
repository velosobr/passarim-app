// A tela de Detalhe: escolhe o que mostrar em cada fase (carregando,
// conteúdo, não encontrada ou erro).

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.detail.domain.Cluster
import com.velosobr.passarim.feature.detail.presentation.resources.Res
import com.velosobr.passarim.feature.detail.presentation.resources.back
import com.velosobr.passarim.feature.detail.presentation.resources.error_generic_body
import com.velosobr.passarim.feature.detail.presentation.resources.error_generic_title
import com.velosobr.passarim.feature.detail.presentation.resources.error_no_connection_body
import com.velosobr.passarim.feature.detail.presentation.resources.error_no_connection_title
import com.velosobr.passarim.feature.detail.presentation.resources.error_rate_limited_body
import com.velosobr.passarim.feature.detail.presentation.resources.error_rate_limited_title
import com.velosobr.passarim.feature.detail.presentation.resources.error_server_body
import com.velosobr.passarim.feature.detail.presentation.resources.error_server_title
import com.velosobr.passarim.feature.detail.presentation.resources.not_found_action
import com.velosobr.passarim.feature.detail.presentation.resources.not_found_body
import com.velosobr.passarim.feature.detail.presentation.resources.not_found_title
import com.velosobr.passarim.feature.detail.presentation.resources.retry
import com.velosobr.piuds.components.PiuHeroIconButton
import com.velosobr.piuds.components.PiuSkeletonBox
import com.velosobr.piuds.components.PiuStateScreen
import com.velosobr.piuds.components.StateTone
import com.velosobr.piuds.resources.ic_arrow_back
import com.velosobr.piuds.resources.ic_bird
import com.velosobr.piuds.resources.ic_cloud_off
import com.velosobr.piuds.resources.ic_wifi_off
import com.velosobr.piuds.theme.PiuDimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.piuds.resources.Res as DesignRes

/**
 * A tela sem estado próprio: tudo vem de [state] e cada interação vira uma
 * [DetailAction]. [mapContent] tem o mapa de verdade como padrão; os snapshots
 * passam outro.
 */
@Composable
fun DetailScreen(
    state: DetailState,
    onAction: (DetailAction) -> Unit,
    mapContent: @Composable (List<Cluster>, Modifier) -> Unit = { clusters, modifier -> SpeciesMap(clusters, modifier) },
    scrollState: ScrollState = rememberScrollState(),
) {
    when (val phase = state.phase) {
        DetailPhase.Loading -> DetailSkeleton(onBack = { onAction(DetailAction.Back) })
        is DetailPhase.Content -> DetailContent(phase.detail, state, onAction, mapContent, scrollState)
        DetailPhase.NotFound ->
            StatePage(onBack = { onAction(DetailAction.Back) }) {
                PiuStateScreen(
                    icon = painterResource(DesignRes.drawable.ic_bird),
                    title = stringResource(Res.string.not_found_title),
                    body = stringResource(Res.string.not_found_body),
                ) {
                    Button(onClick = { onAction(DetailAction.Back) }, modifier = Modifier.height(PiuDimens.touchTarget)) {
                        Text(stringResource(Res.string.not_found_action))
                    }
                }
            }
        is DetailPhase.Error -> {
            val copy = phase.error.copy()
            StatePage(onBack = { onAction(DetailAction.Back) }) {
                PiuStateScreen(
                    icon = painterResource(copy.icon),
                    title = stringResource(copy.title),
                    body = stringResource(copy.body),
                    tone = StateTone.Error,
                ) {
                    Button(
                        onClick = { onAction(DetailAction.Retry) },
                        enabled = state.retryEnabled,
                        modifier = Modifier.height(PiuDimens.touchTarget),
                    ) { Text(stringResource(Res.string.retry)) }
                }
            }
        }
    }
}

/** O "esqueleto" do carregamento, já com o botão de voltar funcionando. */
@Composable
private fun DetailSkeleton(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box {
            PiuSkeletonBox(Modifier.fillMaxWidth().height(300.dp), shape = RectangleShape)
            PiuHeroIconButton(
                painterResource(DesignRes.drawable.ic_arrow_back),
                contentDescription = stringResource(Res.string.back),
                onClick = onBack,
                modifier = Modifier.statusBarsPadding().padding(8.dp),
            )
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PiuSkeletonBox(Modifier.width(250.dp).height(32.dp))
            PiuSkeletonBox(Modifier.width(180.dp).height(18.dp))
            PiuSkeletonBox(Modifier.width(125.dp).height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { PiuSkeletonBox(Modifier.weight(1f).height(64.dp)) }
            }
            PiuSkeletonBox(Modifier.fillMaxWidth().height(72.dp))
            PiuSkeletonBox(Modifier.fillMaxWidth().height(14.dp))
            PiuSkeletonBox(Modifier.fillMaxWidth().height(14.dp))
            PiuSkeletonBox(Modifier.width(285.dp).height(14.dp))
        }
    }
}

/** Página das telas de estado (não encontrada, erro), com o botão de voltar no topo. */
@Composable
private fun StatePage(
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Icon(vectorResource(DesignRes.drawable.ic_arrow_back), contentDescription = stringResource(Res.string.back))
        }
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Box(Modifier.verticalScroll(rememberScrollState()).padding(vertical = 24.dp)) { content() }
        }
    }
}

/** Ícone, título e texto de cada tela de erro. */
private class ErrorCopy(
    val icon: DrawableResource,
    val title: StringResource,
    val body: StringResource,
)

/** Escolhe os textos e o ícone de cada tipo de erro. */
private fun ScreenError.copy(): ErrorCopy =
    when (this) {
        ScreenError.NoConnection ->
            ErrorCopy(DesignRes.drawable.ic_wifi_off, Res.string.error_no_connection_title, Res.string.error_no_connection_body)
        ScreenError.ServiceUnavailable ->
            ErrorCopy(DesignRes.drawable.ic_cloud_off, Res.string.error_server_title, Res.string.error_server_body)
        is ScreenError.RateLimited ->
            ErrorCopy(DesignRes.drawable.ic_cloud_off, Res.string.error_rate_limited_title, Res.string.error_rate_limited_body)
        ScreenError.Generic ->
            ErrorCopy(DesignRes.drawable.ic_cloud_off, Res.string.error_generic_title, Res.string.error_generic_body)
    }
