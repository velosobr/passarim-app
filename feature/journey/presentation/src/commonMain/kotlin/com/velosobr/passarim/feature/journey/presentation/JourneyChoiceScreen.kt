// A tela da Escolha de jornada (prancheta Escolha), no tema do adulto: o
// título, o aviso de que dá para trocar depois e dois cartões grandes, cada
// um com uma prévia do próprio visual. O cartão Kids é desenhado dentro do
// tema Kids, por isso a prévia dele é sempre clara, mesmo com o adulto no
// tema escuro.

package com.velosobr.passarim.feature.journey.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import com.velosobr.passarim.feature.journey.presentation.resources.Res
import com.velosobr.passarim.feature.journey.presentation.resources.choice_adult_body
import com.velosobr.passarim.feature.journey.presentation.resources.choice_adult_title
import com.velosobr.passarim.feature.journey.presentation.resources.choice_hint
import com.velosobr.passarim.feature.journey.presentation.resources.choice_kids_body
import com.velosobr.passarim.feature.journey.presentation.resources.choice_kids_hello
import com.velosobr.passarim.feature.journey.presentation.resources.choice_kids_offline
import com.velosobr.passarim.feature.journey.presentation.resources.choice_kids_retry
import com.velosobr.passarim.feature.journey.presentation.resources.choice_kids_title
import com.velosobr.passarim.feature.journey.presentation.resources.choice_title
import com.velosobr.passarim.feature.journey.presentation.resources.choice_welcome
import com.velosobr.piuds.components.kids.PiuKidsButton
import com.velosobr.piuds.components.kids.PiuPipo
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.toBrush
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** O raio dos dois cartões (prancheta: 28 dp). */
private val CardShape = RoundedCornerShape(28.dp)

/** A altura da faixa de prévia no topo de cada cartão (prancheta: 128 dp). */
private val PreviewHeight = 128.dp

/**
 * A rota da Escolha: liga a tela ao ViewModel. Não recebe callbacks de navegação: escolher grava a
 * jornada, e a raiz do app, que observa as preferências, abre a jornada escolhida. Também não tem
 * "voltar": é a raiz, e o voltar do sistema fecha o app sem gravar nada.
 */
@Composable
fun JourneyChoiceRoot(viewModel: JourneyChoiceViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    JourneyChoiceScreen(state = state, onAction = viewModel::onAction)
}

/**
 * A tela sem estado próprio: o cartão Kids segue [JourneyChoiceState.kids] (conferindo e sem rede
 * aparecem desabilitados; escondido não aparece). A coluna rola, para caber com fonte grande.
 */
@Composable
fun JourneyChoiceScreen(
    state: JourneyChoiceState,
    onAction: (JourneyChoiceAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .safeDrawingPadding()
            .padding(start = 20.dp, top = 48.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(Res.string.choice_welcome).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                stringResource(Res.string.choice_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                stringResource(Res.string.choice_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AdultCard(onClick = { onAction(JourneyChoiceAction.ChooseJourney(Journey.ADULT)) })
        if (state.kids != KidsAvailabilityState.HIDDEN) {
            KidsCard(
                kids = state.kids,
                onClick = { onAction(JourneyChoiceAction.ChooseJourney(Journey.KIDS)) },
                onRetry = { onAction(JourneyChoiceAction.RetryKids) },
            )
        }
    }
}

/**
 * O cartão do Passarim: a prévia é um "mosaico" de três cartões com os gradientes de bioma do Piu
 * sobre o do Pantanal. O cartão é branco no claro e um pouco mais claro que o fundo no escuro
 * (PiuTheme.isDark diz qual dos dois está na tela). clickable com Role.Button junta os textos num
 * botão só para o leitor de tela.
 */
@Composable
private fun AdultCard(onClick: () -> Unit) {
    val gradients = PiuTheme.tokens.gradients
    val cardColor = if (PiuTheme.isDark) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerLowest
    Column(
        Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(cardColor)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(PreviewHeight)
                .background(gradients.getValue(Biome.PANTANAL).toBrush())
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(Biome.AMAZONIA, Biome.CERRADO, Biome.CAATINGA).forEach { biome ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(gradients.getValue(biome).toBrush()),
                )
            }
        }
        CardTexts(stringResource(Res.string.choice_adult_title), stringResource(Res.string.choice_adult_body))
    }
}

/**
 * O cartão do Passarim Kids, desenhado dentro do PiuTheme(journey = KIDS): fundo creme, a sombra
 * sólida dos cartões do Kids, e a prévia com o Pipo sobre o céu do mundo Pantanal. A prévia é
 * decoração (clearAndSetSemantics sem nada): o leitor de tela lê só o nome e a frase do cartão.
 *
 * Só a parte de cima é o botão de entrar; [kids] diz se ela está habilitada. Sem rede, embaixo dela
 * aparecem o aviso e o "Tentar de novo", um botão separado e sempre habilitado.
 */
@Composable
private fun KidsCard(
    kids: KidsAvailabilityState,
    onClick: () -> Unit,
    onRetry: () -> Unit,
) {
    PiuTheme(journey = Journey.KIDS) {
        val colors = MaterialTheme.colorScheme
        val shadow = checkNotNull(PiuTheme.tokens.cardShadow)
        val sky = PiuTheme.tokens.gradients.getValue(Biome.PANTANAL)
        Box(Modifier.fillMaxWidth()) {
            // A sombra sólida: uma caixa da cor da sombra atrás do cartão, aparecendo 4 dp abaixo dele.
            Box(Modifier.matchParentSize().padding(top = shadow.offset).background(shadow.color, CardShape))
            Column(
                Modifier
                    .padding(bottom = shadow.offset)
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(colors.background),
            ) {
                Column(
                    Modifier
                        .alpha(if (kids.canEnter) 1f else 0.5f)
                        .clickable(enabled = kids.canEnter, role = Role.Button, onClick = onClick),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(PreviewHeight)
                            .background(sky.toBrush())
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clearAndSetSemantics { },
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(PiuPipo, contentDescription = null, modifier = Modifier.size(88.dp))
                        Text(stringResource(Res.string.choice_kids_hello), style = MaterialTheme.typography.titleLarge, color = sky.content)
                    }
                    CardTexts(stringResource(Res.string.choice_kids_title), stringResource(Res.string.choice_kids_body))
                }
                if (kids == KidsAvailabilityState.OFFLINE) {
                    Column(
                        Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            stringResource(Res.string.choice_kids_offline),
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface,
                        )
                        PiuKidsButton(stringResource(Res.string.choice_kids_retry), onClick = onRetry)
                    }
                }
            }
        }
    }
}

/** O nome e a frase de um cartão, nas cores e na tipografia do tema em que o cartão está. */
@Composable
private fun CardTexts(
    title: String,
    body: String,
) {
    Column(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(start = 18.dp, top = 16.dp, end = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
