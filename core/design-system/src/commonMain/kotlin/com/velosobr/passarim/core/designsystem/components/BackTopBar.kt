// Barra do topo com o botão de voltar e o título da tela.
//
// Em Compose, a interface é feita de funções @Composable que descrevem o que
// aparece na tela a partir dos parâmetros; quando um parâmetro muda, o
// Compose chama a função de novo (recomposição) e atualiza só o que mudou.
// O Modifier é a corrente de ajustes de um elemento (tamanho, espaçamento,
// cliques, acessibilidade), aplicada na ordem em que é escrita.

package com.velosobr.passarim.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.resources.Res
import com.velosobr.passarim.core.designsystem.resources.back
import com.velosobr.passarim.core.designsystem.resources.ic_arrow_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Botão de voltar (com descrição para o leitor de tela) e o título, marcado
 * como cabeçalho (heading) para quem navega pela tela com o TalkBack ou o
 * VoiceOver.
 */
@Composable
fun BackTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().padding(start = 4.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(vectorResource(Res.drawable.ic_arrow_back), contentDescription = stringResource(Res.string.back))
        }
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp).semantics { heading() },
        )
    }
}
