// A barra de navegação do Kids: escura, flutuante, com raio de 38 dp e 76 dp
// de altura. O item ativo fica amarelo, com ícone e rótulo grandes; os
// outros mostram o ícone e um rótulo pequeno embaixo.

package com.velosobr.piuds.components.kids

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.components.adult.PiuNavItem
import com.velosobr.piuds.theme.PiuKidsDimens
import com.velosobr.piuds.theme.PiuMotion

/**
 * A barra do Kids. O fundo é o inverseSurface do esquema Kids (azul-noite),
 * o item ativo o inversePrimary (o amarelo do Pipo) com o texto em
 * onSurface, e os inativos o inverseOnSurface (o creme). Todo item tem o
 * alvo de 56 dp do Kids. Os rótulos sempre aparecem: no Kids, ícone sozinho
 * não basta para quem ainda está aprendendo a ler os ícones.
 */
@Composable
fun PiuKidsNavBar(
    items: List<PiuNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .clip(RoundedCornerShape(38.dp))
            .background(colors.inverseSurface)
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val active = index == selected
            val itemModifier =
                Modifier
                    .animateContentSize(PiuMotion.state())
                    .defaultMinSize(minWidth = 72.dp, minHeight = PiuKidsDimens.touchTarget)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (active) colors.inversePrimary else colors.inverseSurface)
                    .selectable(selected = active, role = Role.Tab, onClick = { onSelect(index) })
            if (active) {
                Row(
                    itemModifier.padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(item.icon, contentDescription = null, tint = colors.onSurface, modifier = Modifier.size(26.dp))
                    Text(item.label, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                }
            } else {
                Column(
                    itemModifier.padding(horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                ) {
                    Icon(item.icon, contentDescription = null, tint = colors.inverseOnSurface, modifier = Modifier.size(24.dp))
                    Text(item.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = colors.inverseOnSurface)
                }
            }
        }
    }
}
