// Selo do status de conservação de uma espécie.
// @Composable: função que desenha interface; Modifier ajusta tamanho, espaço,
// cliques e acessibilidade (veja PiuBackTopBar.kt).

package com.velosobr.piuds.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.conservation_cr
import com.velosobr.piuds.resources.conservation_dd
import com.velosobr.piuds.resources.conservation_en
import com.velosobr.piuds.resources.conservation_ew
import com.velosobr.piuds.resources.conservation_ex
import com.velosobr.piuds.resources.conservation_lc
import com.velosobr.piuds.resources.conservation_nt
import com.velosobr.piuds.resources.conservation_vu
import com.velosobr.piuds.theme.LocalConservationPalette
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Selo de conservação: ponto + rótulo, com as cores semânticas do tema (min. 4.5:1 de contraste). */
@Composable
fun PiuConservationBadge(
    status: ConservationStatus,
    modifier: Modifier = Modifier,
) {
    val colors = LocalConservationPalette.current[status]
    Row(
        modifier =
            modifier
                .heightIn(min = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.background)
                .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(8.dp).background(colors.content, CircleShape))
        Text(
            text = stringResource(status.label()),
            color = colors.content,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

/** O rótulo traduzível de cada status. */
private fun ConservationStatus.label(): StringResource =
    when (this) {
        ConservationStatus.LC -> Res.string.conservation_lc
        ConservationStatus.NT -> Res.string.conservation_nt
        ConservationStatus.VU -> Res.string.conservation_vu
        ConservationStatus.EN -> Res.string.conservation_en
        ConservationStatus.CR -> Res.string.conservation_cr
        ConservationStatus.EW -> Res.string.conservation_ew
        ConservationStatus.EX -> Res.string.conservation_ex
        ConservationStatus.DD -> Res.string.conservation_dd
    }
