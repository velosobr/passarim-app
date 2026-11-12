// Snapshots da tela de Detalhe.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
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
import com.velosobr.passarim.core.domain.Biome as PrimaryBiome

/**
 * O Detalhe completo (topo, meio e fim, com créditos abertos e o canto pausado), o player com e
 * sem os picos da forma de onda, sem canto, sem foto (o gradiente do bioma), carregando, não
 * encontrado e com erro. O mapa entra como um desenho simples, porque o MapLibre não desenha no
 * Paparazzi. A foto é uma imagem toda branca, o pior caso para o nome em branco sobre ela: o Coil,
 * em modo de inspeção (LocalInspectionMode), pede a imagem ao AsyncImagePreviewHandler em vez de
 * baixar.
 */
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
                    Photo(null, null, "foto-branca", photoCredit),
                    Photo(null, null, null, Credit("(c) Jéssica Martins", "CC BY-NC-SA", "inaturalist", null)),
                    Photo(null, null, null, Credit(null, "CC0", "inaturalist", null)),
                ),
            audio =
                Audio(
                    "https://media/arara.aac",
                    31_000,
                    Credit("Jayrson Araujo de Oliveira", "CC BY-NC-SA", "xeno-canto", null),
                    peaks = PEAKS,
                ),
            whereToFind =
                WhereToFind(
                    states = listOf("AM", "BA", "GO", "MA", "MG", "MS", "MT", "PA", "PI", "TO"),
                    biomes = listOf("amazonia", "cerrado", "pantanal"),
                    clusters = listOf(Cluster(-19.6, -56.4, 197)),
                ),
            primaryBiome = PrimaryBiome.PANTANAL,
        )

    private val complete =
        DetailState(phase = DetailPhase.Content(arara), isFavorite = true, player = PlaybackState.Paused(12_000), creditsExpanded = true)

    @OptIn(ExperimentalCoilApi::class)
    private fun screen(
        name: String,
        state: DetailState,
        scrollPx: Int = 0,
    ) = snap(name) {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(Color.White.toArgb()) },
        ) {
            DetailScreen(
                state = state,
                onAction = {},
                mapContent = { _, modifier -> MapPlaceholder(modifier) },
                scrollState = ScrollState(scrollPx),
            )
        }
    }

    @Test
    fun completeTop() = screen("detail-complete-top", complete)

    @Test
    fun completeMiddle() = screen("detail-complete-middle", complete, scrollPx = MIDDLE_PX)

    @Test
    fun completeBottom() = screen("detail-complete-bottom", complete, scrollPx = Int.MAX_VALUE)

    /** O player com os 64 picos: a parte já tocada (12 de 31 s) na cor primária. */
    @Test
    fun player() = screen("detail-player", complete, scrollPx = PLAYER_PX)

    /** Sem picos (BFF antigo ou canto sem processar): a forma de onda é uma linha reta. */
    @Test
    fun playerWithoutPeaks() =
        screen(
            "detail-player-without-peaks",
            complete.copy(phase = DetailPhase.Content(arara.copy(audio = arara.audio?.copy(peaks = emptyList())))),
            scrollPx = PLAYER_PX,
        )

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

/** Rolagem, em pixels, até o meio da página, para fotografar as seções de baixo. */
private const val MIDDLE_PX = 2_200

/** Rolagem, em pixels, que deixa o player do canto no meio da tela. */
private const val PLAYER_PX = 1_000

/**
 * 64 picos de exemplo, no desenho da prancheta (alturas de 10 a 32 de 32, repetidas): o suficiente
 * para a forma de onda ter altos e baixos.
 */
private val PEAKS =
    listOf(10, 18, 26, 14, 30, 22, 12, 28, 20, 32, 16, 24, 30, 12, 20, 28, 14, 22, 18, 26, 10, 20, 14, 24, 18, 12, 16, 10)
        .let { heights -> List(64) { heights[it % heights.size] / 32f } }

/** Um retângulo com a atribuição no lugar do mapa, do mesmo tamanho dele. */
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
