// Cores dos selos de conservação, por tema.

package com.velosobr.passarim.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.velosobr.passarim.core.domain.ConservationStatus

/** Cor do selo de um status: fundo e texto, com contraste mínimo de 4.5:1 (verificado nos tokens). */
data class ConservationColor(
    val background: Color,
    val content: Color,
)

/** Cores semânticas dos status por tema. Não seguem as cores dinâmicas do sistema. */
data class ConservationPalette(
    private val colors: Map<ConservationStatus, ConservationColor>,
) {
    /** "operator fun get" deixa escrever palette[status] em vez de palette.get(status). */
    operator fun get(status: ConservationStatus): ConservationColor = colors.getValue(status)
}

/** As cores dos selos no tema claro. */
val LightConservationPalette =
    ConservationPalette(
        mapOf(
            ConservationStatus.LC to ConservationColor(background = Color(0xFFCDEBD3), content = Color(0xFF0B3B1C)),
            ConservationStatus.NT to ConservationColor(background = Color(0xFFE3EDB8), content = Color(0xFF33400A)),
            ConservationStatus.VU to ConservationColor(background = Color(0xFFFFE08A), content = Color(0xFF4A3600)),
            ConservationStatus.EN to ConservationColor(background = Color(0xFFFFC9A3), content = Color(0xFF5A2200)),
            ConservationStatus.CR to ConservationColor(background = Color(0xFFFFB4AB), content = Color(0xFF5C0008)),
            ConservationStatus.EW to ConservationColor(background = Color(0xFFE1D5F0), content = Color(0xFF2E1A4D)),
            ConservationStatus.EX to ConservationColor(background = Color(0xFFDDDDDD), content = Color(0xFF1F1F1F)),
            ConservationStatus.DD to ConservationColor(background = Color(0xFFD3E4F5), content = Color(0xFF14304D)),
        ),
    )

/** As cores dos selos no tema escuro. */
val DarkConservationPalette =
    ConservationPalette(
        mapOf(
            ConservationStatus.LC to ConservationColor(background = Color(0xFF1F4A2C), content = Color(0xFFBDF0C8)),
            ConservationStatus.NT to ConservationColor(background = Color(0xFF4A5418), content = Color(0xFFE3EDB8)),
            ConservationStatus.VU to ConservationColor(background = Color(0xFF5E4800), content = Color(0xFFFFE08A)),
            ConservationStatus.EN to ConservationColor(background = Color(0xFF6E3000), content = Color(0xFFFFC9A3)),
            ConservationStatus.CR to ConservationColor(background = Color(0xFF8A1018), content = Color(0xFFFFDAD6)),
            ConservationStatus.EW to ConservationColor(background = Color(0xFF44306A), content = Color(0xFFE8DDFF)),
            ConservationStatus.EX to ConservationColor(background = Color(0xFF3C3C3C), content = Color(0xFFE3E3E3)),
            ConservationStatus.DD to ConservationColor(background = Color(0xFF22405E), content = Color(0xFFD3E4F5)),
        ),
    )

/**
 * A paleta disponível para qualquer componente dentro do tema, sem passar por
 * parâmetro. Um CompositionLocal é um valor que "desce" pela árvore de
 * componentes; static, porque a paleta muda raramente (só ao trocar de tema).
 */
val LocalConservationPalette = staticCompositionLocalOf { LightConservationPalette }
