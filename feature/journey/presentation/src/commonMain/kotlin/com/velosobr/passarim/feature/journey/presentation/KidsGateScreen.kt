// A tela da trava para adultos, no tema Kids (prancheta KidsTrava): o
// "Voltar", a conta por extenso, a mensagem de erro, a caixa com a resposta
// e um teclado próprio de PiuKidsButton. O teclado do sistema não é usado:
// assim ele não cobre a tela e não oferece letras nem sugestões.

package com.velosobr.passarim.feature.journey.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.velosobr.passarim.core.presentation.ObserveAsEvents
import com.velosobr.passarim.feature.journey.presentation.resources.Res
import com.velosobr.passarim.feature.journey.presentation.resources.gate_back
import com.velosobr.passarim.feature.journey.presentation.resources.gate_confirm
import com.velosobr.passarim.feature.journey.presentation.resources.gate_confirm_key
import com.velosobr.passarim.feature.journey.presentation.resources.gate_erase
import com.velosobr.passarim.feature.journey.presentation.resources.gate_erase_key
import com.velosobr.passarim.feature.journey.presentation.resources.gate_only_adults
import com.velosobr.passarim.feature.journey.presentation.resources.gate_question
import com.velosobr.passarim.feature.journey.presentation.resources.gate_wrong
import com.velosobr.passarim.feature.journey.presentation.resources.number_and
import com.velosobr.passarim.feature.journey.presentation.resources.number_exact_hundred
import com.velosobr.passarim.feature.journey.presentation.resources.number_hundreds
import com.velosobr.passarim.feature.journey.presentation.resources.number_tens
import com.velosobr.passarim.feature.journey.presentation.resources.number_units
import com.velosobr.piuds.components.kids.PiuKidsButton
import com.velosobr.piuds.resources.ic_arrow_back
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import com.velosobr.piuds.resources.Res as DesignRes

/** A altura das teclas (prancheta: 64 dp, acima dos 56 dp mínimos do Kids). */
private val KeyHeight = 64.dp

/**
 * A rota da trava: liga a tela ao ViewModel e transforma o "Voltar" em [onBack]. O voltar do sistema
 * não passa por aqui: o NavHost do Kids já fecha a trava sozinho. O acerto também não: o ViewModel
 * grava ADULT, e a raiz do app troca de jornada.
 */
@Composable
fun KidsGateRoot(
    onBack: () -> Unit,
    viewModel: KidsGateViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            KidsGateEvent.NavigateBack -> onBack()
        }
    }
    KidsGateScreen(state = state, onAction = viewModel::onAction)
}

/**
 * A tela sem estado próprio. A pergunta e a mensagem de erro ficam em liveRegion (Polite): o leitor
 * de tela anuncia sozinho quando elas mudam, depois do que estiver falando. A resposta digitada fica
 * fora da árvore de acessibilidade (clearAndSetSemantics sem nada dentro), para o leitor não
 * "soprar" os algarismos em voz alta. A coluna rola: com fonte 2.0×, o teclado passa da altura da
 * tela.
 */
@Composable
fun KidsGateScreen(
    state: KidsGateState,
    onAction: (KidsGateAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    val words = rememberNumberWords()
    val question =
        stringResource(
            Res.string.gate_question,
            numberInWords(state.question.first, words),
            numberInWords(state.question.second, words),
        )
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PiuKidsButton(
            text = stringResource(Res.string.gate_back),
            onClick = { onAction(KidsGateAction.Back) },
            icon = vectorResource(DesignRes.drawable.ic_arrow_back),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                stringResource(Res.string.gate_only_adults),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                question,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier =
                    Modifier.semantics {
                        heading()
                        liveRegion = LiveRegionMode.Polite
                    },
            )
        }
        if (state.wrongAnswer) {
            Text(
                stringResource(Res.string.gate_wrong),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        AnswerBox(state.answer)
        Keypad(onAction)
    }
}

/** A caixa com os algarismos digitados, de contorno grosso como na prancheta, escondida do leitor de tela. */
@Composable
private fun AnswerBox(answer: String) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clearAndSetSemantics { }
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, shape)
            .border(3.dp, MaterialTheme.colorScheme.onSurface, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(answer, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

/**
 * O teclado: 1 a 9 em três linhas e, na última, apagar, 0 e confirmar. Apagar e confirmar mostram um
 * símbolo ("⌫" e "OK"), e o contentDescription diz ao leitor de tela o que eles fazem ("Apagar",
 * "Confirmar"): quando um nó tem descrição, o leitor lê a descrição no lugar do texto.
 */
@Composable
private fun Keypad(onAction: (KidsGateAction) -> Unit) {
    val erase = stringResource(Res.string.gate_erase)
    val confirm = stringResource(Res.string.gate_confirm)
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { row ->
            KeyRow {
                row.forEach { digit -> Key(digit.toString(), onClick = { onAction(KidsGateAction.TypeDigit(digit)) }) }
            }
        }
        KeyRow {
            Key(stringResource(Res.string.gate_erase_key), onClick = { onAction(KidsGateAction.Erase) }, description = erase)
            Key("0", onClick = { onAction(KidsGateAction.TypeDigit(0)) })
            Key(stringResource(Res.string.gate_confirm_key), onClick = { onAction(KidsGateAction.Confirm) }, description = confirm)
        }
    }
}

/** Uma linha de três teclas de larguras iguais (weight(1f) em cada uma, no Key). */
@Composable
private fun KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), content = content)
}

/** Uma tecla: um PiuKidsButton de 64 dp; [description], se houver, é o que o leitor de tela lê. */
@Composable
private fun RowScope.Key(
    label: String,
    onClick: () -> Unit,
    description: String? = null,
) {
    PiuKidsButton(
        text = label,
        onClick = onClick,
        height = KeyHeight,
        modifier =
            Modifier
                .weight(1f)
                .then(if (description != null) Modifier.semantics { contentDescription = description } else Modifier),
    )
}

/** Lê as palavras dos números do strings.xml (stringArrayResource devolve a lista de um string-array). */
@Composable
private fun rememberNumberWords(): NumberWords {
    val units = stringArrayResource(Res.array.number_units)
    val tens = stringArrayResource(Res.array.number_tens)
    val hundreds = stringArrayResource(Res.array.number_hundreds)
    val exactHundred = stringResource(Res.string.number_exact_hundred)
    val and = stringResource(Res.string.number_and)
    return remember(units, tens, hundreds, exactHundred, and) {
        NumberWords(units, tens, hundreds, exactHundred, and)
    }
}
