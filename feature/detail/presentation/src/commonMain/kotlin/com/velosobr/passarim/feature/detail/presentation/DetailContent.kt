package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.ConservationBadge
import com.velosobr.passarim.core.designsystem.components.CreditCaption
import com.velosobr.passarim.core.designsystem.components.FactTile
import com.velosobr.passarim.core.designsystem.components.GalleryPhoto
import com.velosobr.passarim.core.designsystem.components.HeroIconButton
import com.velosobr.passarim.core.designsystem.components.HeroPhoto
import com.velosobr.passarim.core.designsystem.components.InfoChips
import com.velosobr.passarim.core.designsystem.components.LabeledChips
import com.velosobr.passarim.core.designsystem.components.PhotoGallery
import com.velosobr.passarim.core.designsystem.resources.favorite_add
import com.velosobr.passarim.core.designsystem.resources.favorite_remove
import com.velosobr.passarim.core.designsystem.resources.ic_arrow_back
import com.velosobr.passarim.core.designsystem.resources.ic_favorite_filled
import com.velosobr.passarim.core.designsystem.resources.ic_favorites
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.feature.detail.domain.Audio
import com.velosobr.passarim.feature.detail.domain.Cluster
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail
import com.velosobr.passarim.feature.detail.domain.WhereToFind
import com.velosobr.passarim.feature.detail.domain.isSafeLink
import com.velosobr.passarim.feature.detail.presentation.resources.Res
import com.velosobr.passarim.feature.detail.presentation.resources.back
import com.velosobr.passarim.feature.detail.presentation.resources.credit_audio
import com.velosobr.passarim.feature.detail.presentation.resources.credit_open_source
import com.velosobr.passarim.feature.detail.presentation.resources.credit_photo
import com.velosobr.passarim.feature.detail.presentation.resources.credit_photo_numbered
import com.velosobr.passarim.feature.detail.presentation.resources.credit_text
import com.velosobr.passarim.feature.detail.presentation.resources.credit_unknown_author
import com.velosobr.passarim.feature.detail.presentation.resources.credit_unknown_license
import com.velosobr.passarim.feature.detail.presentation.resources.credits_toggle
import com.velosobr.passarim.feature.detail.presentation.resources.fact_biomes
import com.velosobr.passarim.feature.detail.presentation.resources.fact_bullet
import com.velosobr.passarim.feature.detail.presentation.resources.fact_occurrence
import com.velosobr.passarim.feature.detail.presentation.resources.fact_size
import com.velosobr.passarim.feature.detail.presentation.resources.fact_size_value
import com.velosobr.passarim.feature.detail.presentation.resources.fact_states
import com.velosobr.passarim.feature.detail.presentation.resources.family
import com.velosobr.passarim.feature.detail.presentation.resources.section_about
import com.velosobr.passarim.feature.detail.presentation.resources.section_biome
import com.velosobr.passarim.feature.detail.presentation.resources.section_diet
import com.velosobr.passarim.feature.detail.presentation.resources.section_facts
import com.velosobr.passarim.feature.detail.presentation.resources.section_gallery
import com.velosobr.passarim.feature.detail.presentation.resources.section_song
import com.velosobr.passarim.feature.detail.presentation.resources.section_where
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

@Composable
internal fun DetailContent(
    detail: SpeciesDetail,
    state: DetailState,
    onAction: (DetailAction) -> Unit,
    mapContent: @Composable (List<Cluster>, Modifier) -> Unit,
    scrollState: ScrollState,
) {
    val credit = rememberCreditFormatter()
    Box {
        Column(Modifier.fillMaxSize().verticalScroll(scrollState).navigationBarsPadding()) {
            Hero(detail, state.isFavorite, onAction)
            Column(
                Modifier.padding(horizontal = PassarimDimens.screenMargin, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Header(
                    detail,
                    detail.photos
                        .firstOrNull()
                        ?.credit
                        ?.let { stringResource(Res.string.credit_photo, credit(it)) },
                )
                Facts(detail)
                if (detail.biomes.isNotEmpty()) LabeledChips(stringResource(Res.string.section_biome), detail.biomes.map { it.label })
                detail.diet?.let { Diet(it) }
                detail.audio?.let { Song(it, state.player, credit, onAction) }
                if (detail.description.isNotBlank()) {
                    Section(stringResource(Res.string.section_about)) {
                        Text(detail.description, style = MaterialTheme.typography.bodyMedium)
                        CreditCaption(stringResource(Res.string.credit_text, credit(detail.descriptionCredit)))
                    }
                }
                if (detail.facts.isNotEmpty()) {
                    Section(stringResource(Res.string.section_facts)) {
                        detail.facts.forEach {
                            Text(
                                stringResource(Res.string.fact_bullet, it),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                if (detail.photos.size > 1) {
                    Section(stringResource(Res.string.section_gallery)) {
                        PhotoGallery(detail.photos.drop(1).map { GalleryPhoto(it.galleryUrl, credit(it.credit)) })
                    }
                }
                WhereToFindSection(detail.whereToFind, mapContent)
                Credits(detail, state.creditsExpanded, credit, onAction)
            }
        }
        if (scrollState.value > 0) {
            Box(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars).background(MaterialTheme.colorScheme.surface))
        }
    }
}

@Composable
private fun Diet(diet: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(Res.string.section_diet),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(diet, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Song(
    audio: Audio,
    playback: PlaybackState,
    credit: (Credit) -> String,
    onAction: (DetailAction) -> Unit,
) {
    Section(stringResource(Res.string.section_song)) {
        AudioPlayerCard(
            durationMs = audio.durationMs,
            credit = stringResource(Res.string.credit_audio, credit(audio.credit)),
            playback = playback,
            onTogglePlay = { onAction(DetailAction.TogglePlay) },
            onSeek = { onAction(DetailAction.Seek(it)) },
        )
    }
}

@Composable
private fun WhereToFindSection(
    where: WhereToFind,
    mapContent: @Composable (List<Cluster>, Modifier) -> Unit,
) {
    if (where.clusters.isEmpty() && where.states.isEmpty()) return
    Section(stringResource(Res.string.section_where)) {
        mapContent(where.clusters, Modifier)
        if (where.states.isNotEmpty()) InfoChips(where.states)
    }
}

@Composable
private fun Hero(
    detail: SpeciesDetail,
    isFavorite: Boolean,
    onAction: (DetailAction) -> Unit,
) {
    HeroPhoto(imageUrl = detail.photos.firstOrNull()?.heroUrl) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            HeroIconButton(
                painterResource(DesignRes.drawable.ic_arrow_back),
                contentDescription = stringResource(Res.string.back),
                onClick = { onAction(DetailAction.Back) },
            )
            HeroIconButton(
                painterResource(if (isFavorite) DesignRes.drawable.ic_favorite_filled else DesignRes.drawable.ic_favorites),
                contentDescription =
                    stringResource(if (isFavorite) DesignRes.string.favorite_remove else DesignRes.string.favorite_add, detail.commonName),
                onClick = { onAction(DetailAction.ToggleFavorite) },
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun Header(
    detail: SpeciesDetail,
    photoCredit: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        photoCredit?.let { CreditCaption(it) }
        Text(detail.commonName, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(
            detail.scientificName,
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            detail.conservation?.let { ConservationBadge(it, Modifier.align(Alignment.CenterVertically)) }
            Text(
                stringResource(Res.string.family, detail.family),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun Facts(detail: SpeciesDetail) {
    val tiles =
        buildList {
            detail.sizeCm?.let { add(stringResource(Res.string.fact_size_value, it) to stringResource(Res.string.fact_size)) }
            val biomes = detail.biomes.size
            if (biomes > 0) add(pluralStringResource(Res.plurals.fact_biomes, biomes, biomes) to stringResource(Res.string.fact_occurrence))
            val states = detail.whereToFind.states.size
            if (states > 0) add(pluralStringResource(Res.plurals.fact_states, states, states) to stringResource(Res.string.fact_occurrence))
        }
    if (tiles.isEmpty()) return
    if (factsInSingleColumn(LocalDensity.current.fontScale)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tiles.forEach { (value, label) -> FactTile(value, label, Modifier.fillMaxWidth()) }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tiles.forEach { (value, label) -> FactTile(value, label, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        content()
    }
}

@Composable
private fun Credits(
    detail: SpeciesDetail,
    expanded: Boolean,
    credit: (Credit) -> String,
    onAction: (DetailAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = { onAction(DetailAction.ToggleCredits) }, modifier = Modifier.heightIn(min = PassarimDimens.touchTarget)) {
            Text(stringResource(Res.string.credits_toggle))
        }
        if (!expanded) return@Column
        val photos = detail.photos
        photos.forEachIndexed { index, photo ->
            val line =
                if (photos.size == 1) {
                    stringResource(Res.string.credit_photo, credit(photo.credit))
                } else {
                    stringResource(Res.string.credit_photo_numbered, index + 1, credit(photo.credit))
                }
            CreditEntry(line, photo.credit.sourceUrl, onAction)
        }
        detail.audio?.let { CreditEntry(stringResource(Res.string.credit_audio, credit(it.credit)), it.credit.sourceUrl, onAction) }
        CreditEntry(stringResource(Res.string.credit_text, credit(detail.descriptionCredit)), detail.descriptionCredit.sourceUrl, onAction)
    }
}

@Composable
private fun CreditEntry(
    text: String,
    url: String?,
    onAction: (DetailAction) -> Unit,
) {
    Column {
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (url != null && isSafeLink(url)) {
            TextButton(
                onClick = { onAction(DetailAction.OpenCredit(url)) },
                modifier = Modifier.heightIn(min = PassarimDimens.touchTarget),
            ) {
                Text(stringResource(Res.string.credit_open_source))
            }
        }
    }
}

@Composable
private fun rememberCreditFormatter(): (Credit) -> String {
    val unknownAuthor = stringResource(Res.string.credit_unknown_author)
    val unknownLicense = stringResource(Res.string.credit_unknown_license)
    return { creditLine(it, unknownAuthor, unknownLicense) }
}
