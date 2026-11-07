// A barra de navegação flutuante do adulto (frente E): uma pílula com raio
// de 34 dp que flutua sobre o conteúdo, com o item ativo expandido (ícone e
// rótulo) e os outros só com o ícone. É feita sobre Row porque a
// ShortNavigationBar do Material é experimental e não flutua.

package com.velosobr.piuds.components.adult

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.components.PiuBlurState
import com.velosobr.piuds.components.PiuBlurSurface
import com.velosobr.piuds.theme.PiuDimens
import com.velosobr.piuds.theme.PiuMotion

/** Um destino da barra: o ícone e o rótulo (que também é a descrição para o leitor de tela). */
data class PiuNavItem(
    val icon: ImageVector,
    val label: String,
)

/**
 * A barra flutuante. [selected] é o índice do item ativo. O fundo é a
 * superfície com desfoque ([PiuBlurSurface]) sobre o conteúdo marcado com
 * [blurState]. selectableGroup diz ao leitor de tela que os itens formam
 * um grupo em que só um fica marcado.
 *
 * O item ativo cresce com a mola de estado do [PiuMotion]
 * (animateContentSize); com "reduzir animações", a troca é seca.
 */
@Composable
fun PiuFloatingNavBar(
    items: List<PiuNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    blurState: PiuBlurState,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    PiuBlurSurface(
        state = blurState,
        shape = RoundedCornerShape(34.dp),
        tint = colors.surfaceContainerLowest.copy(alpha = 0.82f),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.fillMaxWidth().defaultMinSize(minHeight = 68.dp).padding(horizontal = 8.dp, vertical = 10.dp).selectableGroup(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val active = index == selected
                Row(
                    Modifier
                        .animateContentSize(PiuMotion.state())
                        .defaultMinSize(minWidth = PiuDimens.touchTarget, minHeight = PiuDimens.touchTarget)
                        .background(if (active) colors.secondaryContainer else colors.secondaryContainer.copy(alpha = 0f), RoundedCornerShape(24.dp))
                        .selectable(selected = active, role = Role.Tab, onClick = { onSelect(index) })
                        .padding(horizontal = if (active) 18.dp else 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Ativo, o rótulo já está escrito; inativo, o ícone é tudo o que o leitor de tela tem.
                    Icon(
                        item.icon,
                        contentDescription = if (active) null else item.label,
                        tint = if (active) colors.onSecondaryContainer else colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    if (active) {
                        Text(item.label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = colors.onSecondaryContainer)
                    }
                }
            }
        }
    }
}
