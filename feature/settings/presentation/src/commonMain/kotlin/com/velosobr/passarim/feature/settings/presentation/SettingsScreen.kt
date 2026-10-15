package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.ScreenTitle
import com.velosobr.passarim.core.designsystem.resources.ic_info
import com.velosobr.passarim.core.designsystem.resources.ic_palette
import com.velosobr.passarim.core.designsystem.resources.ic_storage
import com.velosobr.passarim.core.designsystem.resources.ic_theme
import com.velosobr.passarim.core.designsystem.resources.ic_volume
import com.velosobr.passarim.core.domain.ThemeMode
import com.velosobr.passarim.feature.settings.presentation.resources.Res
import com.velosobr.passarim.feature.settings.presentation.resources.about_row
import com.velosobr.passarim.feature.settings.presentation.resources.about_row_body
import com.velosobr.passarim.feature.settings.presentation.resources.autoplay
import com.velosobr.passarim.feature.settings.presentation.resources.autoplay_body
import com.velosobr.passarim.feature.settings.presentation.resources.dynamic_colors
import com.velosobr.passarim.feature.settings.presentation.resources.dynamic_colors_body
import com.velosobr.passarim.feature.settings.presentation.resources.section_about
import com.velosobr.passarim.feature.settings.presentation.resources.section_appearance
import com.velosobr.passarim.feature.settings.presentation.resources.section_data
import com.velosobr.passarim.feature.settings.presentation.resources.section_playback
import com.velosobr.passarim.feature.settings.presentation.resources.settings_title
import com.velosobr.passarim.feature.settings.presentation.resources.storage
import com.velosobr.passarim.feature.settings.presentation.resources.storage_body
import com.velosobr.passarim.feature.settings.presentation.resources.theme
import com.velosobr.passarim.feature.settings.presentation.resources.theme_dark
import com.velosobr.passarim.feature.settings.presentation.resources.theme_light
import com.velosobr.passarim.feature.settings.presentation.resources.theme_system
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

private val ThemeMode.label: StringResource
    get() =
        when (this) {
            ThemeMode.SYSTEM -> Res.string.theme_system
            ThemeMode.LIGHT -> Res.string.theme_light
            ThemeMode.DARK -> Res.string.theme_dark
        }

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 16.dp)) {
        ScreenTitle(stringResource(Res.string.settings_title))
        SectionHeader(stringResource(Res.string.section_appearance))
        SettingsRow(title = stringResource(Res.string.theme), icon = DesignRes.drawable.ic_theme)
        ThemeOptions(state.settings.theme, onAction)
        if (state.dynamicColorsSupported) {
            SwitchRow(
                title = stringResource(Res.string.dynamic_colors),
                subtitle = stringResource(Res.string.dynamic_colors_body),
                icon = DesignRes.drawable.ic_palette,
                checked = state.settings.dynamicColors,
                onCheckedChange = { onAction(SettingsAction.ChangeDynamicColors(it)) },
            )
        }
        SectionHeader(stringResource(Res.string.section_playback))
        SwitchRow(
            title = stringResource(Res.string.autoplay),
            subtitle = stringResource(Res.string.autoplay_body),
            icon = DesignRes.drawable.ic_volume,
            checked = state.settings.autoPlay,
            onCheckedChange = { onAction(SettingsAction.ChangeAutoPlay(it)) },
        )
        SectionHeader(stringResource(Res.string.section_data))
        SettingsRow(
            title = stringResource(Res.string.storage),
            subtitle = stringResource(Res.string.storage_body),
            icon = DesignRes.drawable.ic_storage,
            modifier = Modifier.clickable { onAction(SettingsAction.OpenStorage) },
        ) { Chevron() }
        SectionHeader(stringResource(Res.string.section_about))
        SettingsRow(
            title = stringResource(Res.string.about_row),
            subtitle = stringResource(Res.string.about_row_body, state.versionName),
            icon = DesignRes.drawable.ic_info,
            modifier = Modifier.clickable { onAction(SettingsAction.OpenAbout) },
        ) { Chevron() }
    }
}

@Composable
private fun ThemeOptions(
    selected: ThemeMode,
    onAction: (SettingsAction) -> Unit,
) {
    Column(Modifier.selectableGroup()) {
        ThemeMode.entries.forEach { mode ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .selectable(
                        selected = mode == selected,
                        onClick = { onAction(SettingsAction.SelectTheme(mode)) },
                        role = Role.RadioButton,
                    ).padding(start = RowTextStart, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                RadioButton(selected = mode == selected, onClick = null)
                Text(stringResource(mode.label), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsRow(
        title = title,
        subtitle = subtitle,
        icon = icon,
        modifier = Modifier.toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
    ) {
        Switch(checked = checked, onCheckedChange = null)
    }
}
