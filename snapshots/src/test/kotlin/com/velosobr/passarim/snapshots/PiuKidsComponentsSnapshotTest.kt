// Snapshots dos componentes do Piu Kids (frente C, plano C2): botão,
// cartões de mundo, figurinhas, balão do Pipo e a barra, só no tema claro
// (o Kids não tem escuro) e nas três escalas de fonte.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.adult.PiuNavItem
import com.velosobr.piuds.components.kids.PiuKidsButton
import com.velosobr.piuds.components.kids.PiuKidsNavBar
import com.velosobr.piuds.components.kids.PiuMissingSticker
import com.velosobr.piuds.components.kids.PiuPipoBalloon
import com.velosobr.piuds.components.kids.PiuSticker
import com.velosobr.piuds.components.kids.PiuWorldCard
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_explore
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.resources.ic_play
import com.velosobr.piuds.resources.ic_settings
import org.jetbrains.compose.resources.vectorResource
import org.junit.Test

class PiuKidsComponentsSnapshotTest(
    fontScalePercent: Int,
) : KidsSnapshotTest(fontScalePercent) {
    @Test
    fun buttons() =
        snap("kids-buttons") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PiuKidsButton("Ouvir!", onClick = {}, icon = vectorResource(Res.drawable.ic_play))
                PiuKidsButton("Guardar no meu álbum", onClick = {}, height = 72.dp)
                PiuKidsButton("Desabilitado", onClick = {}, enabled = false)
            }
        }

    @Test
    fun worldCards() =
        snap("kids-world-cards") {
            // Duas colunas: com três, a 2.0× um nome de mundo quebraria no meio da palavra (a grade da frente F
            // escolhe as colunas pelo espaço disponível).
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PiuWorldCard(Biome.AMAZONIA, "Amazônia", onClick = {}, modifier = Modifier.weight(1f))
                    PiuWorldCard(Biome.PANTANAL, "Pantanal", onClick = {}, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PiuWorldCard(Biome.CAATINGA, "Caatinga", onClick = {}, progress = "4 de 9", modifier = Modifier.weight(1f))
                    PiuWorldCard(Biome.MATA_ATLANTICA, "Mata Atlântica", onClick = {}, progress = "0 de 12", modifier = Modifier.weight(1f))
                }
            }
        }

    @Test
    fun stickers() =
        snap("kids-stickers") {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PiuSticker("Tuiuiú", Biome.PANTANAL, imageUrl = null, onClick = {})
                PiuSticker("Arara-azul", Biome.CERRADO, imageUrl = null, onClick = {})
                PiuMissingSticker(label = "Ainda falta", description = "figurinha que falta")
            }
        }

    @Test
    fun pipoBalloonAndNavBar() =
        snap("kids-pipo-and-nav") {
            val items =
                listOf(
                    PiuNavItem(vectorResource(Res.drawable.ic_explore), "Explorar"),
                    PiuNavItem(vectorResource(Res.drawable.ic_favorites), "Álbum"),
                    PiuNavItem(vectorResource(Res.drawable.ic_settings), "Ajustes"),
                )
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                PiuPipoBalloon("Qual ave vamos conhecer hoje?")
                PiuKidsNavBar(items, selected = 0, onSelect = {})
                PiuKidsNavBar(items, selected = 1, onSelect = {})
            }
        }
}
