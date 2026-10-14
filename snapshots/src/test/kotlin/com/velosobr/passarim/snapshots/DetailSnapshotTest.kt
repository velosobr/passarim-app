package com.velosobr.passarim.snapshots

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.core.presentation.ScreenError
import com.velosobr.passarim.feature.detail.domain.Audio
import com.velosobr.passarim.feature.detail.domain.Biome
import com.velosobr.passarim.feature.detail.domain.Cluster
import com.velosobr.passarim.feature.detail.domain.Photo
import com.velosobr.passarim.feature.detail.domain.PlaybackState
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail
import com.velosobr.passarim.feature.detail.domain.WhereToFind
import com.velosobr.passarim.feature.detail.presentation.DetailPhase
import com.velosobr.passarim.feature.detail.presentation.DetailScreen
import com.velosobr.passarim.feature.detail.presentation.DetailState
import com.velosobr.passarim.feature.detail.presentation.MAP_HEIGHT
import com.velosobr.passarim.feature.detail.presentation.MapAttribution
import org.junit.Test

class DetailSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private val photoCredit = Credit("(c) Paul Donahue", "CC BY-NC", "inaturalist", "https://www.inaturalist.org/observations/87436377")

    private val arara =
        SpeciesDetail(
            id = "anodorhynchus-hyacinthinus",
            commonName = "Arara-azul-grande",
            scientificName = "Anodorhynchus hyacinthinus",
            family = "Psittacidae",
            conservation = ConservationStatus.VU,
            sizeCm = 100,
            diet = "Sementes duras de palmeiras, como coco-de-urucuri e coco-de-espinho, quebradas com a força do bico.",
            description =
                "A arara-azul-grande é a maior espécie de arara do mundo, com plumagem azul-cobalto intensa e um anel " +
                    "amarelo ao redor dos olhos e na base do bico.",
            descriptionCredit = Credit("Equipe Passarim", "CC BY 4.0", "curated", "https://pt.wikipedia.org/wiki/Arara-azul-grande"),
            facts =
                listOf(
                    "Formam casais monogâmicos que permanecem juntos durante toda a vida.",
                    "Já foram registradas usando folhas mastigadas como ferramentas.",
                ),
            biomes = listOf(Biome("amazonia", "Amazônia"), Biome("cerrado", "Cerrado"), Biome("pantanal", "Pantanal")),
            photos =
                listOf(
                    Photo(null, null, null, photoCredit),
                    Photo(null, null, null, Credit("(c) Jéssica Martins", "CC BY-NC-SA", "inaturalist", null)),
                    Photo(null, null, null, Credit(null, "CC0", "inaturalist", null)),
                ),
            audio = Audio("https://media/arara.aac", 31_000, Credit("Jayrson Araujo de Oliveira", "CC BY-NC-SA", "xeno-canto", null)),
            whereToFind =
                WhereToFind(
                    states = listOf("AM", "BA", "GO", "MA", "MG", "MS", "MT", "PA", "PI", "TO"),
                    biomes = listOf("amazonia", "cerrado", "pantanal"),
                    clusters = listOf(Cluster(-19.6, -56.4, 197)),
                ),
        )

    private val complete =
        DetailState(phase = DetailPhase.Content(arara), isFavorite = true, player = PlaybackState.Paused(12_000), creditsExpanded = true)

    private fun screen(
        name: String,
        state: DetailState,
        scrollPx: Int = 0,
    ) = snap(name) {
        DetailScreen(
            state = state,
            onAction = {},
            mapContent = { _, modifier -> MapPlaceholder(modifier) },
            scrollState = ScrollState(scrollPx),
        )
    }

    @Test
    fun completeTop() = screen("detail-complete-top", complete)

    @Test
    fun completeMiddle() = screen("detail-complete-middle", complete, scrollPx = MIDDLE_PX)

    @Test
    fun completeBottom() = screen("detail-complete-bottom", complete, scrollPx = Int.MAX_VALUE)

    @Test
    fun withoutAudio() = screen("detail-without-audio", DetailState(phase = DetailPhase.Content(arara.copy(audio = null))), MIDDLE_PX)

    @Test
    fun withoutPhoto() =
        screen(
            "detail-without-photo",
            DetailState(
                phase =
                    DetailPhase.Content(
                        arara.copy(photos = emptyList(), conservation = null, sizeCm = null, diet = null, facts = emptyList()),
                    ),
                player = PlaybackState.Error,
            ),
        )

    @Test
    fun loading() = screen("detail-loading", DetailState(phase = DetailPhase.Loading))

    @Test
    fun notFound() = screen("detail-not-found", DetailState(phase = DetailPhase.NotFound))

    @Test
    fun errorServer() = screen("detail-error-server", DetailState(phase = DetailPhase.Error(ScreenError.ServiceUnavailable)))
}

private const val MIDDLE_PX = 2_200

@Composable
private fun MapPlaceholder(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(MAP_HEIGHT)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        MapAttribution(Modifier.align(Alignment.BottomStart))
    }
}
