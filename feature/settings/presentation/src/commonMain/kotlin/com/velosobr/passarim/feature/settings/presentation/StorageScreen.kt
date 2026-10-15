package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.BackTopBar
import com.velosobr.passarim.core.designsystem.resources.ic_check
import com.velosobr.passarim.core.designsystem.resources.ic_delete
import com.velosobr.passarim.core.designsystem.resources.ic_favorites
import com.velosobr.passarim.core.designsystem.resources.ic_image
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens
import com.velosobr.passarim.feature.settings.presentation.resources.Res
import com.velosobr.passarim.feature.settings.presentation.resources.cache_cleared
import com.velosobr.passarim.feature.settings.presentation.resources.clear_cache
import com.velosobr.passarim.feature.settings.presentation.resources.favorites
import com.velosobr.passarim.feature.settings.presentation.resources.favorites_kept
import com.velosobr.passarim.feature.settings.presentation.resources.image_cache
import com.velosobr.passarim.feature.settings.presentation.resources.image_cache_body
import com.velosobr.passarim.feature.settings.presentation.resources.storage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

@Composable
fun StorageScreen(
    state: StorageState,
    onAction: (StorageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackTopBar(stringResource(Res.string.storage), onBack = { onAction(StorageAction.Back) })
        SettingsRow(
            title = stringResource(Res.string.image_cache),
            subtitle = stringResource(Res.string.image_cache_body),
            icon = DesignRes.drawable.ic_image,
        )
        Row(
            Modifier.padding(horizontal = PassarimDimens.screenMargin, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FilledTonalButton(onClick = { onAction(StorageAction.ClearCache) }, enabled = state != StorageState.Clearing) {
                Icon(vectorResource(DesignRes.drawable.ic_delete), contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(Res.string.clear_cache), modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (state == StorageState.Cleared) {
            Row(
                Modifier
                    .padding(horizontal = PassarimDimens.screenMargin, vertical = 4.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(vectorResource(DesignRes.drawable.ic_check), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    stringResource(Res.string.cache_cleared),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        SettingsRow(
            title = stringResource(Res.string.favorites),
            subtitle = stringResource(Res.string.favorites_kept),
            icon = DesignRes.drawable.ic_favorites,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
