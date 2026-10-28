// Formas e espaçamentos do tema, a partir dos tokens.

package com.velosobr.piuds.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Formas dos tokens: 12dp (cards), 16dp (campos), 28dp (chips e botões grandes). */
val PiuShapes =
    Shapes(
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(28.dp),
    )

/**
 * Espaçamento dos tokens: grade de 4dp, margem de tela 16dp, alvo de toque mínimo 48dp.
 * dp ("density-independent pixel") é a unidade de tamanho que se ajusta à
 * densidade da tela: 48dp têm aproximadamente o mesmo tamanho físico em
 * qualquer aparelho.
 */
object PiuDimens {
    val unit = 4.dp
    val screenMargin = 16.dp
    val touchTarget = 48.dp
}

/**
 * Formas do Piu Kids (bloco "kids.shape" dos tokens): mais redondas que as do
 * adulto. extraLarge (38dp) é o raio da barra de navegação Kids.
 */
val PiuKidsShapes =
    Shapes(
        small = RoundedCornerShape(16.dp),
        medium = RoundedCornerShape(24.dp),
        large = RoundedCornerShape(28.dp),
        extraLarge = RoundedCornerShape(38.dp),
    )

/** Espaçamentos do Kids: o alvo de toque é maior (56dp) porque o dedo da criança é menos preciso. */
object PiuKidsDimens {
    val touchTarget = 56.dp
}
