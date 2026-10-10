package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.FilterChip
import com.velosobr.passarim.core.designsystem.components.SearchField
import com.velosobr.passarim.core.designsystem.components.SkeletonBox
import com.velosobr.passarim.core.designsystem.components.SpeciesCard
import com.velosobr.passarim.core.designsystem.components.StateScreen
import com.velosobr.passarim.core.designsystem.components.StateTone
import com.velosobr.passarim.core.designsystem.resources.Res
import com.velosobr.passarim.core.designsystem.resources.ic_wifi_off
import org.jetbrains.compose.resources.painterResource
import org.junit.Test

/** Os componentes genéricos novos da 5b, isolados (as telas de Explorar ficam em `ExploreSnapshotTest`). */
class ComponentsSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    @Test
    fun speciesCards() =
        snap("species-cards") {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SpeciesCard(
                    name = "Arara-azul-grande",
                    scientificName = "Anodorhynchus hyacinthinus",
                    imageUrl = null,
                    isFavorite = true,
                    onToggleFavorite = {},
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                SpeciesCard(
                    name = "Bem-te-vi",
                    scientificName = "Pitangus sulphuratus",
                    imageUrl = null,
                    isFavorite = false,
                    onToggleFavorite = {},
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }

    @Test
    fun searchAndChips() =
        snap("search-and-chips") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SearchField(value = "", onValueChange = {}, placeholder = "Buscar aves", modifier = Modifier.fillMaxWidth())
                SearchField(value = "azul", onValueChange = {}, placeholder = "Buscar aves", modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(label = "Bioma", selected = false, onClick = {}, showDropdown = true)
                    FilterChip(label = "Pampa", selected = true, onClick = {}, onClear = {})
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(label = "Cerrado · 26", selected = true, onClick = {})
                    FilterChip(label = "Mata Atlântica · 29", selected = false, onClick = {})
                }
            }
        }

    @Test
    fun stateScreenAndSkeleton() =
        snap("state-and-skeleton") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StateScreen(
                    icon = painterResource(Res.drawable.ic_wifi_off),
                    title = "Sem conexão",
                    body = "Verifique sua internet e tente de novo.",
                    tone = StateTone.Error,
                ) {
                    Button(onClick = {}) { Text("Tentar novamente") }
                }
                SkeletonBox(Modifier.width(160.dp).height(14.dp))
            }
        }
}
