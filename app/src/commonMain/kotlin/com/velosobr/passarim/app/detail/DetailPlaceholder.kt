package com.velosobr.passarim.app.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.velosobr.passarim.app.resources.Res
import com.velosobr.passarim.app.resources.back
import com.velosobr.passarim.app.resources.placeholder_in_progress
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens
import org.jetbrains.compose.resources.stringResource

/** Detalhe provisório: mostra o id recebido. A tela real chega na 5c. */
@Composable
fun DetailPlaceholder(
    id: String,
    onBack: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(PassarimDimens.screenMargin),
        verticalArrangement = Arrangement.spacedBy(PassarimDimens.screenMargin, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(id, style = MaterialTheme.typography.titleLarge)
        Text(stringResource(Res.string.placeholder_in_progress))
        TextButton(onClick = onBack) { Text(stringResource(Res.string.back)) }
    }
}
