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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_explore
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.resources.ic_settings
import com.velosobr.piuds.theme.PiuKidsDimens
import com.velosobr.piuds.theme.PiuMotion
import com.velosobr.piuds.theme.PiuTheme
import org.jetbrains.compose.resources.vectorResource

/**
 * A barra do Kids. O fundo é o inverseSurface do esquema Kids (azul-noite),
 * o item ativo o inversePrimary (o amarelo do Pipo) com o texto em
 * onSurface, e os inativos o inverseOnSurface (o creme). Todo item tem o
 * alvo de 56 dp do Kids. Os rótulos sempre aparecem: no Kids, ícone sozinho
 * não basta para quem ainda está aprendendo a ler os ícones. Cada rótulo
 * fica numa linha só e encolhe o necessário para caber com fonte grande
 * ("Ajustes" a 2.0× quebraria no meio da palavra).
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
            // weight divide a largura que sobra: o ativo pode crescer até uns 45% da barra e cada
            // inativo até uns 28%. fill = false: ninguém é esticado além do que o conteúdo pede.
            val itemModifier =
                Modifier
                    .weight(if (active) 1.6f else 1f, fill = false)
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
                    OneLineLabel(item.label, MaterialTheme.typography.titleMedium.copy(color = colors.onSurface))
                }
            } else {
                Column(
                    itemModifier.padding(horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                ) {
                    Icon(item.icon, contentDescription = null, tint = colors.inverseOnSurface, modifier = Modifier.size(24.dp))
                    OneLineLabel(
                        item.label,
                        MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, color = colors.inverseOnSurface),
                    )
                }
            }
        }
    }
}

/** Um rótulo numa linha só: autoSize escolhe o maior tamanho, até o do [style], em que ele cabe. */
@Composable
private fun OneLineLabel(
    text: String,
    style: TextStyle,
) {
    BasicText(
        text,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = style.fontSize),
    )
}

/** Prévia: a barra do Kids com "Explorar" ativo. */
@Preview
@Composable
private fun PiuKidsNavBarPreview() {
    PiuTheme(journey = Journey.KIDS) {
        PiuKidsNavBar(
            items =
                listOf(
                    PiuNavItem(vectorResource(Res.drawable.ic_explore), "Explorar"),
                    PiuNavItem(vectorResource(Res.drawable.ic_favorites), "Álbum"),
                    PiuNavItem(vectorResource(Res.drawable.ic_settings), "Ajustes"),
                ),
            selected = 0,
            onSelect = {},
        )
    }
}
