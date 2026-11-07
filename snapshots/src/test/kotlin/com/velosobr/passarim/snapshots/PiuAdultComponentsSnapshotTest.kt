// Snapshots dos componentes novos do Piu (frente C, plano C2): os comuns
// (forma cookie, superfície com desfoque, crédito com e sem link) e os da
// jornada adulta, em claro e escuro e nas três escalas de fonte.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.PiuBlurSurface
import com.velosobr.piuds.components.PiuCookieShape
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.components.adult.PiuBiomeChip
import com.velosobr.piuds.components.adult.PiuFeaturedCard
import com.velosobr.piuds.components.adult.PiuFloatingNavBar
import com.velosobr.piuds.components.adult.PiuMosaicCard
import com.velosobr.piuds.components.adult.PiuMosaicHeight
import com.velosobr.piuds.components.adult.PiuNavItem
import com.velosobr.piuds.components.piuBlurSource
import com.velosobr.piuds.components.rememberPiuBlurState
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_explore
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.resources.ic_settings
import org.jetbrains.compose.resources.vectorResource
import org.junit.Test

/** Os componentes sem foto: o fundo é o gradiente do bioma, como no app antes de a foto carregar. */
class PiuAdultComponentsSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
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
                    onPlay = {},
                    playDescription = "Ouvir o canto",
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

    @Test
    fun biomeChips() =
        snap("biome-chips") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PiuBiomeChip(Biome.AMAZONIA, "Amazônia", selected = true, onClick = {})
                    PiuBiomeChip(Biome.CERRADO, "Cerrado", selected = false, onClick = {})
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PiuBiomeChip(Biome.MATA_ATLANTICA, "Mata Atlântica", selected = false, onClick = {})
                    PiuBiomeChip(Biome.PANTANAL, "Pantanal", selected = false, onClick = {})
                }
            }
        }

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
                Box(Modifier.fillMaxWidth().height(96.dp).piuBlurSource(blur).background(Color(0xFF3E9CB5)))
                PiuFloatingNavBar(items, selected = 0, onSelect = {}, blurState = blur, modifier = Modifier.padding(top = 14.dp, start = 8.dp, end = 8.dp))
            }
        }

    @Test
    fun commonComponents() =
        snap("common-components") {
            val blur = rememberPiuBlurState()
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp).background(MaterialTheme.colorScheme.primary, PiuCookieShape()))
                    Box(Modifier.size(72.dp)) {
                        Box(Modifier.size(72.dp).piuBlurSource(blur).background(Color(0xFFD9A13B)))
                        PiuBlurSurface(blur, PiuCookieShape(), MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), Modifier.size(56.dp).align(Alignment.Center)) {
                            Text("A", Modifier.align(Alignment.Center))
                        }
                    }
                }
                PiuCreditCaption("Foto: Fulano · CC BY-NC", onOpenSource = {})
                PiuCreditCaption("Foto: Fulano · CC BY-NC", onOpenSource = {}, linkEnabled = false)
            }
        }
}
