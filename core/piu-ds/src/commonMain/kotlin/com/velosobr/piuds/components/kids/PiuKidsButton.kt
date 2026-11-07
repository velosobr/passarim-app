// O botão "gordinho" do Kids: raio total, de 56 a 72 dp de altura, com a
// sombra sólida que afunda ao tocar. O rótulo é azul-noite sobre o laranja:
// o branco da prancheta não passava no contraste (spec da frente C, 4.3).

package com.velosobr.piuds.components.kids

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.theme.PiuKidsDimens
import com.velosobr.piuds.theme.PiuTheme

/**
 * Botão do Kids. [height] vai de 56 dp (o alvo mínimo do Kids) a 72 dp. A
 * face é a cor primaryContainer do esquema Kids (o laranja) com o rótulo em
 * onPrimaryContainer (azul-noite), e a sombra é a "buttonShadow" dos tokens.
 * [interactionSource] é opcional, como nos botões do Material: serve para
 * acompanhar (ou simular, nos snapshots) o toque.
 */
@Composable
fun PiuKidsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = PiuKidsDimens.touchTarget,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MaterialTheme.colorScheme
    val shadow = checkNotNull(PiuTheme.tokens.buttonShadow) { "PiuKidsButton só existe dentro do PiuTheme(journey = KIDS)" }
    PiuSinkingSurface(
        face = SolidColor(colors.primaryContainer),
        shadowColor = shadow.color,
        shadowOffset = shadow.offset,
        shape = RoundedCornerShape(percent = 50),
        minHeight = height.coerceIn(PiuKidsDimens.touchTarget, 72.dp),
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
    ) {
        Row(
            Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(26.dp))
            }
            Text(text, style = MaterialTheme.typography.titleMedium, color = colors.onPrimaryContainer)
        }
    }
}

/** Prévia: o botão normal, o alto e o desabilitado, no tema Kids. */
@Preview
@Composable
private fun PiuKidsButtonPreview() {
    PiuTheme(journey = Journey.KIDS) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PiuKidsButton("Ouvir!", onClick = {})
            PiuKidsButton("Guardar no meu álbum", onClick = {}, height = 72.dp)
            PiuKidsButton("Desabilitado", onClick = {}, enabled = false)
        }
    }
}
