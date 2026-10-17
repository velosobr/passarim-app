// A tela Sobre: nome e versão do app, créditos das fontes de dados e os
// links para a política de privacidade e as licenças.

package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.BackTopBar
import com.velosobr.passarim.core.designsystem.resources.ic_bird
import com.velosobr.passarim.core.designsystem.resources.ic_info
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens
import com.velosobr.passarim.feature.settings.presentation.resources.Res
import com.velosobr.passarim.feature.settings.presentation.resources.about_title
import com.velosobr.passarim.feature.settings.presentation.resources.app_name
import com.velosobr.passarim.feature.settings.presentation.resources.credit_map
import com.velosobr.passarim.feature.settings.presentation.resources.credit_map_source
import com.velosobr.passarim.feature.settings.presentation.resources.credit_songs
import com.velosobr.passarim.feature.settings.presentation.resources.credit_songs_source
import com.velosobr.passarim.feature.settings.presentation.resources.credit_species
import com.velosobr.passarim.feature.settings.presentation.resources.credit_species_source
import com.velosobr.passarim.feature.settings.presentation.resources.credit_texts
import com.velosobr.passarim.feature.settings.presentation.resources.credit_texts_source
import com.velosobr.passarim.feature.settings.presentation.resources.credits_body
import com.velosobr.passarim.feature.settings.presentation.resources.credits_title
import com.velosobr.passarim.feature.settings.presentation.resources.open_source_licenses
import com.velosobr.passarim.feature.settings.presentation.resources.privacy_policy
import com.velosobr.passarim.feature.settings.presentation.resources.privacy_policy_body
import com.velosobr.passarim.feature.settings.presentation.resources.version
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

/** Os créditos das fontes: o que veio de onde (cantos, textos, espécies e mapa). */
private val credits =
    listOf(
        Res.string.credit_songs to Res.string.credit_songs_source,
        Res.string.credit_texts to Res.string.credit_texts_source,
        Res.string.credit_species to Res.string.credit_species_source,
        Res.string.credit_map to Res.string.credit_map_source,
    )

/** A tela Sobre, sem estado próprio. systemBarsPadding afasta o conteúdo das barras do sistema. */
@Composable
fun AboutScreen(
    state: AboutState,
    onAction: (AboutAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp),
    ) {
        BackTopBar(stringResource(Res.string.about_title), onBack = { onAction(AboutAction.Back) })
        AppHeader(state.versionName)
        Text(
            stringResource(Res.string.credits_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = PassarimDimens.screenMargin).padding(top = 24.dp).semantics { heading() },
        )
        Text(
            stringResource(Res.string.credits_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = PassarimDimens.screenMargin, vertical = 8.dp),
        )
        credits.forEach { (title, source) -> CreditRow(title, source) }
        SettingsRow(
            title = stringResource(Res.string.privacy_policy),
            subtitle = stringResource(Res.string.privacy_policy_body),
            icon = DesignRes.drawable.ic_info,
            modifier = Modifier.padding(top = 16.dp).clickable { onAction(AboutAction.OpenPrivacyPolicy) },
        ) { Chevron() }
        SettingsRow(
            title = stringResource(Res.string.open_source_licenses),
            icon = DesignRes.drawable.ic_info,
            modifier = Modifier.clickable { onAction(AboutAction.OpenLicenses) },
        ) { Chevron() }
    }
}

/** O ícone, o nome e a versão do app. */
@Composable
private fun AppHeader(versionName: String) {
    Column(
        Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.size(96.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
            Icon(
                vectorResource(DesignRes.drawable.ic_bird),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp),
            )
        }
        Text(
            stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            stringResource(Res.string.version, versionName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Uma linha de crédito: o que é e de onde vem. */
@Composable
private fun CreditRow(
    title: StringResource,
    source: StringResource,
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = PassarimDimens.screenMargin, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(stringResource(title), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        Text(stringResource(source), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
