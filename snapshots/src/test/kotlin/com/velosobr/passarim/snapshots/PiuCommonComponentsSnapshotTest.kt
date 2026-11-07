// Snapshots dos componentes comuns do PiuDS (frente C, plano C2): os que as
// duas jornadas usam, fotografados no tema do adulto, em claro e escuro e
// nas três escalas de fonte.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.components.PiuBlurSurface
import com.velosobr.piuds.components.PiuCookieShape
import com.velosobr.piuds.components.PiuCreditCaption
import com.velosobr.piuds.components.piuBlurSource
import com.velosobr.piuds.components.rememberPiuBlurState
import org.junit.Test

class PiuCommonComponentsSnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    /** O crédito com link (sublinhado, na cor primária) e com o link desligado, como no Kids. */
    @Test
    fun credits() =
        snap("credits") {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PiuCreditCaption("Foto: Fulano · CC BY-NC", onOpenSource = {})
                PiuCreditCaption("Foto: Fulano · CC BY-NC", onOpenSource = {}, linkEnabled = false)
                PiuCreditCaption("Canto: Beltrano · CC BY-SA")
            }
        }

    /**
     * A forma cookie pintada com a primária e a superfície com desfoque sobre um fundo amarelo. O
     * Paparazzi desenha com o renderizador do Android (o mesmo do RenderEffect), então o desfoque
     * aparece na imagem.
     */
    @Test
    fun cookieAndBlur() =
        snap("cookie-and-blur") {
            val blur = rememberPiuBlurState()
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(72.dp).background(MaterialTheme.colorScheme.primary, PiuCookieShape()))
                Box(Modifier.size(96.dp)) {
                    Stripes(Modifier.matchParentSize().piuBlurSource(blur))
                    PiuBlurSurface(
                        state = blur,
                        shape = PiuCookieShape(),
                        tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        modifier = Modifier.size(72.dp).align(Alignment.Center),
                    ) {
                        Text("A", Modifier.align(Alignment.Center))
                    }
                }
            }
        }

    /** Listras pretas e amarelas: num fundo liso, não daria para ver se o desfoque rodou. */
    @Composable
    private fun Stripes(modifier: Modifier) {
        Row(modifier) {
            repeat(8) { i -> Box(Modifier.weight(1f).fillMaxHeight().background(if (i % 2 == 0) Color(0xFFD9A13B) else Color.Black)) }
        }
    }
}
