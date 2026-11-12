// As peças da aba Configurações no visual da frente E3 (prancheta
// FavoritosConfiguracoes): cada seção é um título em verde e um cartão de
// cantos de 24 dp com as linhas dentro, separadas por um fio. O tema é um
// controle de botões segmentados. Ficam neste arquivo, e não no PiuDS, porque
// só esta tela os usa.

package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.velosobr.piuds.theme.PiuDimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

/** O raio dos cartões das seções (prancheta: 24 dp). */
private val GroupShape = RoundedCornerShape(24.dp)

/**
 * A cor do cartão: branco no tema claro (surfaceContainerLowest), como na prancheta. No escuro, o
 * "lowest" é mais escuro que o fundo e o cartão sumiria, então usamos o surfaceContainer, um pouco
 * mais claro que o fundo. luminance() mede o brilho da cor de fundo (0 é preto, 1 é branco) e vale
 * também com as cores dinâmicas do papel de parede.
 */
@Composable
private fun groupColor(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.background.luminance() > 0.5f) scheme.surfaceContainerLowest else scheme.surfaceContainer
}

/**
 * Uma seção: o título (verde, marcado como cabeçalho para o leitor de tela poder pular de seção em
 * seção) e o cartão com as linhas. ColumnScope no [content] deixa as linhas usarem os modificadores
 * de coluna; clip recorta o fundo das linhas (o realce do toque) nos cantos arredondados.
 */
@Composable
internal fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.padding(horizontal = PiuDimens.screenMargin), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp).semantics { heading() },
        )
        Column(Modifier.fillMaxWidth().clip(GroupShape).background(groupColor()), content = content)
    }
}

/** O fio entre duas linhas do mesmo cartão. */
@Composable
internal fun GroupDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
}

/** O ícone da linha num círculo de 36 dp com a cor de destaque suave do tema (secondaryContainer). */
@Composable
private fun RowIcon(icon: DrawableResource) {
    Box(Modifier.size(36.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape), contentAlignment = Alignment.Center) {
        Icon(
            vectorResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** O título da linha e, se houver, o subtítulo embaixo, mais apagado. */
@Composable
private fun RowTexts(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Uma linha do cartão: ícone, título, subtítulo e um slot à direita (a chave ou a seta). O
 * [modifier] recebe o toque (clickable, toggleable) e vale na linha toda; 56 dp de altura mínima
 * passa dos 48 dp de alvo de toque.
 */
@Composable
internal fun GroupRow(
    title: String,
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        RowIcon(icon)
        RowTexts(title, subtitle, Modifier.weight(1f))
        trailing()
    }
}

/**
 * Uma linha com um controle que pode descer para baixo do título quando não cabe ao lado dele (o
 * tema com fonte grande). FlowRow é uma linha que "quebra": o que não cabe vai para a linha de
 * baixo, como palavras num parágrafo; SpaceBetween joga o controle para a direita quando cabe.
 */
@Composable
internal fun GroupFlowRow(
    title: String,
    icon: DrawableResource,
    control: @Composable () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        RowIcon(icon)
        FlowRow(
            Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            RowTexts(title, subtitle = null)
            control()
        }
    }
}

/** Uma opção de um controle segmentado: o rótulo e se está marcada. */
internal class Segment(
    val label: String,
    val selected: Boolean,
    val onClick: () -> Unit,
)

/**
 * Botões segmentados: as opções coladas num contorno de pílula, com a marcada pintada. Ficam lado a
 * lado só se couberem inteiras na largura disponível; senão, uma embaixo da outra, na largura toda
 * (empilhar, nunca cortar o texto). [alwaysStacked] empilha mesmo quando caberia (fonte grande).
 * selectableGroup junta as opções num grupo para o leitor de tela, e cada opção é selectable com
 * Role.RadioButton: ele anuncia "botão de opção, selecionado". Cada opção tem 48 dp de altura, o
 * alvo mínimo de toque.
 *
 * A decisão é por medida, não pela escala de fonte: num aparelho estreito o controle pode não caber
 * nem com fonte 1.0×, e uma Row comum daria ao último botão só o que sobrou, quebrando "Escuro" no
 * meio. Layout é o jeito de medir e posicionar os filhos à mão: maxIntrinsicWidth pergunta a cada
 * opção "quanto você mede sem quebrar linha?" antes de medir de verdade, e a soma decide o arranjo.
 */
@Composable
internal fun SegmentedControl(
    segments: List<Segment>,
    alwaysStacked: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    Layout(
        content = { segments.forEach { SegmentButton(it) } },
        modifier = modifier.selectableGroup().clip(shape).border(1.dp, MaterialTheme.colorScheme.outline, shape),
    ) { measurables, constraints ->
        val widths = measurables.map { it.maxIntrinsicWidth(Constraints.Infinity) }
        val sideBySide = !alwaysStacked && widths.sum() <= constraints.maxWidth
        if (sideBySide) {
            // Lado a lado: cada opção na largura do próprio rótulo e todas na altura da mais alta.
            val height = measurables.zip(widths).maxOf { (measurable, width) -> measurable.maxIntrinsicHeight(width) }
            val placeables = measurables.zip(widths).map { (measurable, width) -> measurable.measure(Constraints.fixed(width, height)) }
            layout(constraints.constrainWidth(widths.sum()), constraints.constrainHeight(height)) {
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += placeable.width
                }
            }
        } else {
            // Empilhadas: todas na largura toda (ou na da opção mais larga, se a largura não tiver limite).
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else widths.max()
            val placeables = measurables.map { it.measure(Constraints(minWidth = width, maxWidth = width)) }
            layout(width, constraints.constrainHeight(placeables.sumOf { it.height })) {
                var y = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(0, y)
                    y += placeable.height
                }
            }
        }
    }
}

/** Uma opção do controle segmentado; a marcada ganha o fundo de destaque suave do tema. */
@Composable
private fun SegmentButton(
    segment: Segment,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier
            .heightIn(min = PiuDimens.touchTarget)
            .background(if (segment.selected) colors.secondaryContainer else Color.Transparent)
            .selectable(selected = segment.selected, onClick = segment.onClick, role = Role.RadioButton)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            segment.label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (segment.selected) colors.onSecondaryContainer else colors.onSurface,
        )
    }
}
