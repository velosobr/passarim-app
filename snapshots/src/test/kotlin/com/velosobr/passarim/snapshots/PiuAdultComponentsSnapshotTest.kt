// Snapshots dos componentes da jornada adulta do PiuDS (frente C, plano C2),
// em claro e escuro e nas três escalas de fonte. Sem foto: o fundo dos cards
// é o gradiente do bioma, como no app antes de a foto carregar.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.adult.PiuBiomeChip
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
}
