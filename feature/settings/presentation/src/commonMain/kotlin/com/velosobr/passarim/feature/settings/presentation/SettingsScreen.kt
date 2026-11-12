// A aba Configurações (frente E3): as seções Aparência, Reprodução, Dados e
// Sobre, cada uma num cartão de cantos arredondados (SettingsCards.kt). A seção
// "Para crianças" entra com a frente D.

package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
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
import com.velosobr.passarim.feature.settings.presentation.resources.storage_body
import com.velosobr.passarim.feature.settings.presentation.resources.storage_row
import com.velosobr.passarim.feature.settings.presentation.resources.theme
import com.velosobr.passarim.feature.settings.presentation.resources.theme_dark
import com.velosobr.passarim.feature.settings.presentation.resources.theme_light
import com.velosobr.passarim.feature.settings.presentation.resources.theme_system
import com.velosobr.piuds.components.PiuScreenTitle
import com.velosobr.piuds.components.adult.PIU_STACK_FONT_SCALE
import com.velosobr.piuds.resources.ic_info
import com.velosobr.piuds.resources.ic_palette
import com.velosobr.piuds.resources.ic_storage
import com.velosobr.piuds.resources.ic_theme
import com.velosobr.piuds.resources.ic_volume
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.velosobr.piuds.resources.Res as DesignRes

/** O rótulo traduzível de cada opção de tema. */
private val ThemeMode.label: StringResource
    get() =
        when (this) {
            ThemeMode.SYSTEM -> Res.string.theme_system
            ThemeMode.LIGHT -> Res.string.theme_light
            ThemeMode.DARK -> Res.string.theme_dark
        }

/**
 * A tela sem estado próprio: tudo vem de [state] e cada toque vira uma [SettingsAction]. A opção de
 * cores dinâmicas só aparece onde o sistema tem. [contentPadding] é o espaço da barra flutuante: a
 * tela rola por baixo dela e o último cartão termina acima dela.
 */
@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = contentPadding.calculateBottomPadding() + 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PiuScreenTitle(stringResource(Res.string.settings_title), Modifier.padding(bottom = 0.dp))
        SettingsGroup(stringResource(Res.string.section_appearance)) {
            GroupFlowRow(title = stringResource(Res.string.theme), icon = DesignRes.drawable.ic_theme) {
                ThemeSegments(state.settings.theme, onAction)
            }
            if (state.dynamicColorsSupported) {
                GroupDivider()
                SwitchRow(
                    title = stringResource(Res.string.dynamic_colors),
                    subtitle = stringResource(Res.string.dynamic_colors_body),
                    icon = DesignRes.drawable.ic_palette,
                    checked = state.settings.dynamicColors,
                    onCheckedChange = { onAction(SettingsAction.ChangeDynamicColors(it)) },
                )
            }
        }
        SettingsGroup(stringResource(Res.string.section_playback)) {
            SwitchRow(
                title = stringResource(Res.string.autoplay),
                subtitle = stringResource(Res.string.autoplay_body),
                icon = DesignRes.drawable.ic_volume,
                checked = state.settings.autoPlay,
                onCheckedChange = { onAction(SettingsAction.ChangeAutoPlay(it)) },
            )
        }
        SettingsGroup(stringResource(Res.string.section_data)) {
            GroupRow(
                title = stringResource(Res.string.storage_row),
                subtitle = stringResource(Res.string.storage_body),
                icon = DesignRes.drawable.ic_storage,
                modifier = Modifier.clickable { onAction(SettingsAction.OpenStorage) },
            ) { Chevron() }
        }
        SettingsGroup(stringResource(Res.string.section_about)) {
            GroupRow(
                title = stringResource(Res.string.about_row),
                subtitle = stringResource(Res.string.about_row_body, state.versionName),
                icon = DesignRes.drawable.ic_info,
                modifier = Modifier.clickable { onAction(SettingsAction.OpenAbout) },
            ) { Chevron() }
        }
    }
}

/**
 * O tema como botões segmentados (Sistema, Claro, Escuro). Com fonte a partir de 1.5×, as opções
 * ficam uma embaixo da outra (PIU_STACK_FONT_SCALE, a mesma regra que põe o mosaico em uma coluna).
 */
@Composable
private fun ThemeSegments(
    selected: ThemeMode,
    onAction: (SettingsAction) -> Unit,
) {
    SegmentedControl(
        segments =
            ThemeMode.entries.map { mode ->
                Segment(stringResource(mode.label), selected = mode == selected, onClick = { onAction(SettingsAction.SelectTheme(mode)) })
            },
        stacked = LocalDensity.current.fontScale >= PIU_STACK_FONT_SCALE,
    )
}

/** Uma linha com chave liga/desliga; o toque vale na linha toda (toggleable) e a chave só mostra o estado. */
@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    GroupRow(
        title = title,
        subtitle = subtitle,
        icon = icon,
        modifier = Modifier.toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
    ) {
        Switch(checked = checked, onCheckedChange = null)
    }
}
