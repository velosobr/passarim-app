// O balão de fala do Pipo ("Qual ave vamos conhecer hoje?"), com o Pipo ao
// lado. O Pipo aqui é decoração: o leitor de tela lê só a frase do balão.

package com.velosobr.piuds.components.kids

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.piuds.theme.PiuTheme

/**
 * O Pipo de 64 dp e o balão branco com a [text]. O balão tem raio de 22 dp e
 * a sombra sólida dos cartões do Kids, 3 dp abaixo (desenhada como uma caixa
 * da cor da sombra atrás do balão).
 */
@Composable
fun PiuPipoBalloon(
    text: String,
    modifier: Modifier = Modifier,
) {
    val shadow = checkNotNull(PiuTheme.tokens.cardShadow) { "PiuPipoBalloon só existe dentro do PiuTheme(journey = KIDS)" }
    val shape = RoundedCornerShape(22.dp)
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(PiuPipo, contentDescription = null, modifier = Modifier.size(64.dp))
        Box(Modifier.weight(1f)) {
            Box(Modifier.matchParentSize().padding(top = 3.dp).background(shadow.color, shape))
            Text(
                text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier =
                    Modifier
                        .padding(bottom = 3.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest, shape)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

/** Prévia do balão com a frase da prancheta. */
@Preview
@Composable
private fun PiuPipoBalloonPreview() {
    PiuTheme(journey = Journey.KIDS) {
        PiuPipoBalloon("Qual ave vamos conhecer hoje?")
    }
}
