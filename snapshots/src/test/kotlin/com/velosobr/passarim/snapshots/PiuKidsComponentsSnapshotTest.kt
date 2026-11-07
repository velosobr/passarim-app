// Snapshots dos componentes do Piu Kids (frente C, plano C2), só no tema
// claro (o Kids não tem escuro) e nas três escalas de fonte. O Pipo não muda
// com a fonte, mas fica na mesma matriz para a base Kids ser uma só.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.components.kids.PiuKidsButton
import com.velosobr.piuds.components.kids.PiuPipo
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_play
import org.jetbrains.compose.resources.vectorResource
import org.junit.Test

class PiuKidsComponentsSnapshotTest(
    fontScalePercent: Int,
) : KidsSnapshotTest(fontScalePercent) {
    /** O Pipo em três tamanhos: o vetor tem de continuar nítido do ícone ao destaque. */
    @Test
    fun pipo() =
        snap("kids-pipo") {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Bottom) {
                Image(PiuPipo, contentDescription = null, modifier = Modifier.size(32.dp))
                Image(PiuPipo, contentDescription = null, modifier = Modifier.size(64.dp))
                Image(PiuPipo, contentDescription = null, modifier = Modifier.size(120.dp))
            }
        }

    /**
     * O botão normal com ícone, o alto (72 dp) e o desabilitado. O pressionado não aparece aqui: o
     * Compose só fica sabendo do toque por um efeito (LaunchedEffect), e o Paparazzi fotografa antes
     * de os efeitos rodarem. O afundar é conferido no PiuKidsButtonTest, do :core:piu-ds.
     */
    @Test
    fun buttons() =
        snap("kids-buttons") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PiuKidsButton("Ouvir!", onClick = {}, icon = vectorResource(Res.drawable.ic_play))
                PiuKidsButton("Guardar no meu álbum", onClick = {}, height = 72.dp)
                PiuKidsButton("Desabilitado", onClick = {}, enabled = false)
            }
        }
}
