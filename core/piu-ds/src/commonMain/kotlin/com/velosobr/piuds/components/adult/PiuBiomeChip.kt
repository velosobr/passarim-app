// Chip de filtro por bioma do Explorar adulto: uma bolinha com o gradiente
// do bioma e o nome. Selecionado, o chip ganha a cor primária, como o chip
// "Todos" da prancheta.

package com.velosobr.piuds.components.adult

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.theme.PiuDimens
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.toBrush

/**
 * O chip de um bioma. [label] é o nome já traduzido ("Amazônia"). selectable
 * dá ao leitor de tela o papel de opção marcável e o estado
 * "selecionado/não selecionado". A altura mínima é o alvo de 48 dp.
 */
@Composable
fun PiuBiomeChip(
    biome: Biome,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (selected) colors.primary else colors.surface,
        contentColor = if (selected) colors.onPrimary else colors.onSurface,
        border = if (selected) null else BorderStroke(1.dp, colors.outlineVariant),
        modifier = modifier.defaultMinSize(minHeight = PiuDimens.touchTarget).selectable(selected = selected, role = Role.Tab, onClick = onClick),
    ) {
        Row(
            Modifier.padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(28.dp).background(PiuTheme.tokens.gradients.getValue(biome).toBrush(), CircleShape))
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
