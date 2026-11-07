// Snapshots dos componentes comuns do PiuDS (frente C, plano C2): os que as
// duas jornadas usam, fotografados no tema do adulto, em claro e escuro e
// nas três escalas de fonte.

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.components.PiuCreditCaption
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
}
