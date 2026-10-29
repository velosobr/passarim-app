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
import com.velosobr.piuds.components.GalleryPhoto
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.components.PiuFactTile
import com.velosobr.piuds.components.PiuFilterChip
import com.velosobr.piuds.components.PiuHeroIconButton
import com.velosobr.piuds.components.PiuHeroPhoto
import com.velosobr.piuds.components.PiuLabeledChips
import com.velosobr.piuds.components.PiuPhotoGallery
import com.velosobr.piuds.components.PiuSearchField
import com.velosobr.piuds.components.PiuSkeletonBox
import com.velosobr.piuds.components.PiuSpeciesCard
import com.velosobr.piuds.components.PiuStateScreen
import com.velosobr.piuds.components.StateTone
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_arrow_back
import com.velosobr.piuds.resources.ic_favorite_filled
import com.velosobr.piuds.resources.ic_wifi_off
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
                PiuSpeciesCard(
                    name = "Arara-azul-grande",
                    scientificName = "Anodorhynchus hyacinthinus",
                    imageUrl = null,
                    isFavorite = true,
                    onToggleFavorite = {},
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                PiuSpeciesCard(
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
                PiuSearchField(value = "", onValueChange = {}, placeholder = "Buscar aves", modifier = Modifier.fillMaxWidth())
                PiuSearchField(value = "azul", onValueChange = {}, placeholder = "Buscar aves", modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PiuFilterChip(label = "Bioma", selected = false, onClick = {}, showDropdown = true)
                    PiuFilterChip(label = "Pampa", selected = true, onClick = {}, onClear = {})
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PiuFilterChip(label = "Cerrado · 26", selected = true, onClick = {})
                    PiuFilterChip(label = "Mata Atlântica · 29", selected = false, onClick = {})
                }
            }
        }

    @Test
    fun stateScreenAndSkeleton() =
        snap("state-and-skeleton") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PiuStateScreen(
                    icon = painterResource(Res.drawable.ic_wifi_off),
                    title = "Sem conexão",
                    body = "Verifique sua internet e tente de novo.",
                    tone = StateTone.Error,
                ) {
                    Button(onClick = {}) { Text("Tentar novamente") }
                }
                PiuSkeletonBox(Modifier.width(160.dp).height(14.dp))
            }
        }

    @Test
    fun detailComponents() =
        snap("detail-components") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PiuHeroPhoto(imageUrl = null) {
                    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        PiuHeroIconButton(painterResource(Res.drawable.ic_arrow_back), contentDescription = "Voltar", onClick = {})
                        PiuHeroIconButton(
                            painterResource(Res.drawable.ic_favorite_filled),
                            contentDescription = "Desfavoritar",
                            onClick = {},
                        )
                    }
                }
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PiuFactTile(value = "100 cm", label = "Tamanho")
                    PiuLabeledChips(label = "Bioma", chips = listOf("Pantanal", "Cerrado", "Amazônia"))
                    PiuCreditCaption("Texto: Equipe Passarim · CC BY 4.0")
                    PiuPhotoGallery(
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
