// Campo de busca do Explorar.
//
// O campo não guarda o texto sozinho: quem chama passa [value] e recebe cada
// mudança em [onValueChange] ("state hoisting", o estado fica com quem usa).
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja PiuBackTopBar.kt).

package com.velosobr.piuds.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_close
import com.velosobr.piuds.resources.ic_search
import com.velosobr.piuds.resources.search_clear
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** Campo de busca em pílula: lupa à esquerda e, com texto, um X que limpa. Uma linha só. */
@Composable
fun PiuSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = 56.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge) },
        leadingIcon = { Icon(vectorResource(Res.drawable.ic_search), contentDescription = null) },
        trailingIcon =
            if (value.isNotEmpty()) {
                {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(vectorResource(Res.drawable.ic_close), contentDescription = stringResource(Res.string.search_clear))
                    }
                }
            } else {
                null
            },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        shape = MaterialTheme.shapes.large,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceContainer,
                unfocusedContainerColor = colors.surfaceContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLeadingIconColor = colors.onSurfaceVariant,
                unfocusedLeadingIconColor = colors.onSurfaceVariant,
                focusedPlaceholderColor = colors.onSurfaceVariant,
                unfocusedPlaceholderColor = colors.onSurfaceVariant,
            ),
    )
}
