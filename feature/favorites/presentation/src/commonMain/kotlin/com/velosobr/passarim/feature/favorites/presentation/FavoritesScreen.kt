package com.velosobr.passarim.feature.favorites.presentation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.ScreenTitle
import com.velosobr.passarim.core.designsystem.components.SpeciesListItem
import com.velosobr.passarim.core.designsystem.components.StateScreen
import com.velosobr.passarim.core.designsystem.resources.ic_favorites
import com.velosobr.passarim.core.designsystem.theme.PassarimDimens
import com.velosobr.passarim.feature.favorites.presentation.resources.Res
import com.velosobr.passarim.feature.favorites.presentation.resources.empty_body
import com.velosobr.passarim.feature.favorites.presentation.resources.empty_title
import com.velosobr.passarim.feature.favorites.presentation.resources.explore_birds
import com.velosobr.passarim.feature.favorites.presentation.resources.favorites_subtitle
import com.velosobr.passarim.feature.favorites.presentation.resources.favorites_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onAction: (FavoritesAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    when (state) {
        FavoritesState.Loading -> ScreenTitle(stringResource(Res.string.favorites_title), modifier)
        FavoritesState.Empty -> EmptyFavorites(onAction, modifier)
        is FavoritesState.Content -> FavoritesList(state, onAction, listState, modifier)
    }
}

@Composable
private fun FavoritesList(
    state: FavoritesState.Content,
    onAction: (FavoritesAction) -> Unit,
    listState: LazyListState,
    modifier: Modifier,
) {
    LazyColumn(modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Column {
                ScreenTitle(stringResource(Res.string.favorites_title))
                Text(
                    stringResource(Res.string.favorites_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = PassarimDimens.screenMargin, end = PassarimDimens.screenMargin, bottom = 16.dp),
                )
            }
        }
        items(state.items, key = { it.id }) { summary ->
            SpeciesListItem(
                name = summary.commonName,
                scientificName = summary.scientificName,
                imageUrl = summary.thumbnailUrl,
                isFavorite = true,
                onToggleFavorite = { onAction(FavoritesAction.ToggleFavorite(summary)) },
                onClick = { onAction(FavoritesAction.OpenDetail(summary.id)) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun EmptyFavorites(
    onAction: (FavoritesAction) -> Unit,
    modifier: Modifier,
) {
    Column(modifier.fillMaxSize()) {
        ScreenTitle(stringResource(Res.string.favorites_title))
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            StateScreen(
                icon = painterResource(DesignRes.drawable.ic_favorites),
                title = stringResource(Res.string.empty_title),
                body = stringResource(Res.string.empty_body),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(
                            rememberScrollState(),
                        ).heightIn(min = maxHeight)
                        .padding(vertical = 24.dp),
            ) {
                Button(onClick = { onAction(FavoritesAction.ExploreClick) }) {
                    Text(stringResource(Res.string.explore_birds))
                }
            }
        }
    }
}
