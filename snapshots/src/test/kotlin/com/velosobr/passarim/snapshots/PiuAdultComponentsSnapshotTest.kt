// Snapshots dos componentes da jornada adulta do PiuDS (frente C, plano C2),
// em claro e escuro e nas três escalas de fonte. Sem foto: o fundo dos cards
// é o gradiente do bioma, como no app antes de a foto carregar.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.adult.PiuBiomeChip
import com.velosobr.piuds.components.adult.PiuFeaturedCard
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.components.adult.PiuMosaicHeight
import com.velosobr.piuds.components.adult.PiuPlayAction
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
}
