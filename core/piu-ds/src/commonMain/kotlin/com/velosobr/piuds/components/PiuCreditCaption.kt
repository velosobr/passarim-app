// Legenda de crédito (autor · licença), em letra pequena, com ou sem link
// para a fonte original.
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja PiuBackTopBar.kt).

package com.velosobr.piuds.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Mostra o crédito de uma foto ou de um canto, como as licenças CC exigem.
 *
 * Com [onOpenSource], o crédito vira um link sublinhado para a fonte
 * original. [linkEnabled] = false desliga o link mesmo assim: é o modo da
 * jornada Kids, em que nenhuma tela abre um site de fora. O texto do
 * crédito aparece igual nos dois modos.
 */
@Composable
fun PiuCreditCaption(
    text: String,
    modifier: Modifier = Modifier,
    onOpenSource: (() -> Unit)? = null,
    linkEnabled: Boolean = true,
) {
    val link = if (linkEnabled) onOpenSource else null
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = if (link != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        textDecoration = if (link != null) TextDecoration.Underline else null,
        // Role.Button faz o leitor de tela anunciar que o crédito pode ser tocado.
        modifier = if (link != null) modifier.clickable(role = Role.Button, onClick = link) else modifier,
    )
}

/**
 * @Preview desenha a função no painel de pré-visualização do Android Studio,
 * sem rodar o app, aqui no tema claro e no escuro. Os snapshots do módulo
 * snapshots são a referência conferida no CI; a prévia é só para quem está
 * editando o componente.
 */
@Preview
@Composable
private fun PiuCreditCaptionPreview() {
    PiuLightAndDarkPreview {
        Column {
            PiuCreditCaption("Foto: Fulano · CC BY-NC", onOpenSource = {})
            PiuCreditCaption("Foto: Fulano · CC BY-NC", onOpenSource = {}, linkEnabled = false)
        }
    }
}
