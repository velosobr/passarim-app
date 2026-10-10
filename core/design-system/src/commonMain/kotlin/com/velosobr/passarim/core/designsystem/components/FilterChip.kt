package com.velosobr.passarim.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.resources.Res
import com.velosobr.passarim.core.designsystem.resources.filter_remove
import com.velosobr.passarim.core.designsystem.resources.ic_check
import com.velosobr.passarim.core.designsystem.resources.ic_close
import com.velosobr.passarim.core.designsystem.resources.ic_expand_more
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.material3.FilterChip as MaterialFilterChip

/**
 * Chip de filtro em pílula. Selecionado: fundo `secondaryContainer` e um ✓ à esquerda. À direita, uma
 * seta ([showDropdown], abre o seletor) ou um X ([onClear], remove o filtro).
 */
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDropdown: Boolean = false,
    onClear: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    MaterialFilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        modifier = modifier.heightIn(min = 36.dp),
        leadingIcon =
            if (selected) {
                { Icon(vectorResource(Res.drawable.ic_check), contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else {
                null
            },
        trailingIcon =
            when {
                onClear != null -> {
                    {
                        Icon(
                            vectorResource(Res.drawable.ic_close),
                            contentDescription = stringResource(Res.string.filter_remove, label),
                            modifier = Modifier.size(18.dp).clickable(onClick = onClear),
                        )
                    }
                }
                showDropdown -> {
                    { Icon(vectorResource(Res.drawable.ic_expand_more), contentDescription = null, modifier = Modifier.size(18.dp)) }
                }
                else -> null
            },
        shape = MaterialTheme.shapes.large,
        colors =
            FilterChipDefaults.filterChipColors(
                labelColor = colors.onSurfaceVariant,
                iconColor = colors.onSurfaceVariant,
                selectedContainerColor = colors.secondaryContainer,
                selectedLabelColor = colors.onSecondaryContainer,
                selectedLeadingIconColor = colors.onSecondaryContainer,
                selectedTrailingIconColor = colors.onSecondaryContainer,
            ),
        border =
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = colors.outlineVariant,
                selectedBorderColor = Color.Transparent,
            ),
    )
}
