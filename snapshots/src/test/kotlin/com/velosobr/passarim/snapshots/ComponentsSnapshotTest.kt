// Snapshots dos componentes do design system.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.designsystem.components.CreditCaption
import com.velosobr.passarim.core.designsystem.components.FactTile
import com.velosobr.passarim.core.designsystem.components.FilterChip
import com.velosobr.passarim.core.designsystem.components.GalleryPhoto
import com.velosobr.passarim.core.designsystem.components.HeroIconButton
import com.velosobr.passarim.core.designsystem.components.HeroPhoto
import com.velosobr.passarim.core.designsystem.components.LabeledChips
import com.velosobr.passarim.core.designsystem.components.PhotoGallery
import com.velosobr.passarim.core.designsystem.components.SearchField
import com.velosobr.passarim.core.designsystem.components.SkeletonBox
import com.velosobr.passarim.core.designsystem.components.SpeciesCard
import com.velosobr.passarim.core.designsystem.components.StateScreen
import com.velosobr.passarim.core.designsystem.components.StateTone
import com.velosobr.passarim.core.designsystem.resources.Res
import com.velosobr.passarim.core.designsystem.resources.ic_arrow_back
import com.velosobr.passarim.core.designsystem.resources.ic_favorite_filled
import com.velosobr.passarim.core.designsystem.resources.ic_wifi_off
import org.jetbrains.compose.resources.painterResource
import org.junit.Test

/** Cards, busca, chips e outros componentes, em todos os temas e escalas de fonte. */
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

    @Test
    fun detailComponents() =
        snap("detail-components") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HeroPhoto(imageUrl = null) {
                    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        HeroIconButton(painterResource(Res.drawable.ic_arrow_back), contentDescription = "Voltar", onClick = {})
                        HeroIconButton(painterResource(Res.drawable.ic_favorite_filled), contentDescription = "Desfavoritar", onClick = {})
                    }
                }
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FactTile(value = "100 cm", label = "Tamanho")
                    LabeledChips(label = "Bioma", chips = listOf("Pantanal", "Cerrado", "Amazônia"))
                    CreditCaption("Texto: Equipe Passarim · CC BY 4.0")
                    PhotoGallery(
                        photos =
                            listOf(
                                GalleryPhoto(null, "Paul Donahue · CC BY-NC"),
                                GalleryPhoto(null, "Jéssica Martins · CC BY-NC-SA"),
                                GalleryPhoto(null, "Autor não informado · CC0"),
                            ),
                    )
                }
            }
        }
}
