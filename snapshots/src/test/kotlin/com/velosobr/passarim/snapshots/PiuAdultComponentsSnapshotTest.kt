// Snapshots dos componentes da jornada adulta do PiuDS (frente C, plano C2),
// em claro e escuro e nas três escalas de fonte. Sem foto: o fundo dos cards
// é o gradiente do bioma, como no app antes de a foto carregar.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.components.adult.PiuBiomeChip
import com.velosobr.piuds.components.adult.PiuFeaturedCard
import com.velosobr.piuds.components.adult.PiuFloatingNavBar
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.components.adult.PiuMosaicHeight
import com.velosobr.piuds.components.adult.PiuPlayAction
import com.velosobr.piuds.components.piuBlurSource
import com.velosobr.piuds.components.rememberPiuBlurState
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_explore
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.resources.ic_settings
import org.jetbrains.compose.resources.vectorResource
import org.junit.Test

class PiuAdultComponentsSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    /** Os seis biomas, o primeiro marcado. FlowRow quebra a linha quando a fonte cresce. */
    @OptIn(ExperimentalLayoutApi::class)
    @Test
    fun biomeChips() =
        snap("biome-chips") {
            FlowRow(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    Biome.AMAZONIA to "Amazônia",
                    Biome.CERRADO to "Cerrado",
                    Biome.MATA_ATLANTICA to "Mata Atlântica",
                    Biome.CAATINGA to "Caatinga",
                    Biome.PANTANAL to "Pantanal",
                    Biome.PAMPA to "Pampa",
                ).forEachIndexed { index, (biome, label) ->
                    PiuBiomeChip(biome, label, selected = index == 0, onClick = {})
                }
            }
        }

    /**
     * Um card alto e favorito ao lado de um baixo: o nome longo quebra linha em vez de cortar. Lado
     * a lado a 2.0× é o pior caso (o nome científico quebra no meio da palavra): a grade do app passa
     * a uma coluna com fonte a partir de 1.5×, regra que a frente E mantém no mosaico.
     */
    @Test
    fun mosaicCards() =
        snap("mosaic-cards") {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PiuMosaicCard(
                    name = "Arara-azul-grande",
                    scientificName = "Anodorhynchus hyacinthinus",
                    imageUrl = null,
                    biome = Biome.PANTANAL,
                    isFavorite = true,
                    onToggleFavorite = {},
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                PiuMosaicCard(
                    name = "Carcará",
                    scientificName = "Caracara plancus",
                    imageUrl = null,
                    biome = Biome.CAATINGA,
                    isFavorite = false,
                    onToggleFavorite = {},
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    height = PiuMosaicHeight.SHORT,
                )
            }
        }

    /** A "Ave do dia" tocando o canto (ícone de pausa) e outra sem canto (sem o botão). */
    @Test
    fun featuredCard() =
        snap("featured-card") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PiuFeaturedCard(
                    badge = "AVE DO DIA · PANTANAL",
                    name = "Tuiuiú",
                    scientificName = "Jabiru mycteria",
                    imageUrl = null,
                    biome = Biome.PANTANAL,
                    onClick = {},
                    play = PiuPlayAction(description = "Pausar o canto do Tuiuiú", playing = true, onClick = {}),
                )
                PiuFeaturedCard(
                    badge = "AVE DO DIA · CERRADO",
                    name = "Seriema",
                    scientificName = "Cariama cristata",
                    imageUrl = null,
                    biome = Biome.CERRADO,
                    onClick = {},
                )
            }
        }

    /**
     * A barra flutuante sobre um fundo colorido (o que ela desfoca), com o primeiro e com o último
     * item ativos: "Configurações" é o rótulo mais longo e tem de caber a 2.0×.
     */
    @Test
    fun floatingNavBar() =
        snap("floating-nav-bar") {
            val blur = rememberPiuBlurState()
            val items =
                listOf(
                    PiuNavItem(vectorResource(Res.drawable.ic_explore), "Explorar"),
                    PiuNavItem(vectorResource(Res.drawable.ic_favorites), "Favoritos"),
                    PiuNavItem(vectorResource(Res.drawable.ic_settings), "Configurações"),
                )
            Box(Modifier.padding(16.dp)) {
                Box(Modifier.matchParentSize().piuBlurSource(blur).background(Color(0xFF3E9CB5)))
                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PiuFloatingNavBar(items, selected = 0, onSelect = {}, blurState = blur)
                    PiuFloatingNavBar(items, selected = 2, onSelect = {}, blurState = blur)
                }
            }
        }

    /**
     * O pior caso do texto sobre foto: uma foto toda branca (céu, garça). O Coil, em modo de
     * inspeção (prévias e snapshots), pergunta ao AsyncImagePreviewHandler que imagem mostrar em vez
     * de baixar: aqui, um ColorImage branco. O nome, o nome científico, o selo e o coração precisam
     * continuar legíveis nas três escalas de fonte.
     */
    @OptIn(ExperimentalCoilApi::class)
    @Test
    fun cardsOverWhitePhoto() =
        snap("cards-over-white-photo") {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(Color.White.toArgb()) },
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PiuMosaicCard(
                            name = "Garça-branca-grande",
                            scientificName = "Ardea alba",
                            imageUrl = "foto-branca",
                            biome = Biome.PANTANAL,
                            isFavorite = false,
                            onToggleFavorite = {},
                            onClick = {},
                            modifier = Modifier.weight(1f),
                        )
                        PiuMosaicCard(
                            name = "Carcará",
                            scientificName = "Caracara plancus",
                            imageUrl = "foto-branca",
                            biome = Biome.CAATINGA,
                            isFavorite = true,
                            onToggleFavorite = {},
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            height = PiuMosaicHeight.SHORT,
                        )
                    }
                    PiuFeaturedCard(
                        badge = "AVE DO DIA · PANTANAL",
                        name = "Tuiuiú",
                        scientificName = "Jabiru mycteria",
                        imageUrl = "foto-branca",
                        biome = Biome.PANTANAL,
                        onClick = {},
                        play = PiuPlayAction(description = "Ouvir o canto do Tuiuiú", playing = false, onClick = {}),
                    )
                }
            }
        }
}
