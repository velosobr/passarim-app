// Formatação de textos do Detalhe: tempo do canto, linha de crédito e o
// layout dos fatos com fonte grande.

package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.core.domain.Credit

/** A partir desta escala de fonte, os fatos passam a uma coluna só. */
const val SINGLE_COLUMN_FONT_SCALE = 1.5f
private const val MILLIS_PER_SECOND = 1_000
private const val SECONDS_PER_MINUTE = 60

/** Se os fatos ficam empilhados para a escala de fonte dada. */
fun factsInSingleColumn(fontScale: Float): Boolean = fontScale >= SINGLE_COLUMN_FONT_SCALE

/** Tempo no formato "m:ss" (1500 ms vira "0:01"); negativo conta como zero. */
fun formatTime(millis: Long): String {
    val totalSeconds = millis.coerceAtLeast(0) / MILLIS_PER_SECOND
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return "${totalSeconds / SECONDS_PER_MINUTE}:${seconds.toString().padStart(2, '0')}"
}

/** A linha "autor · licença", com os textos de "desconhecido" quando faltar algo. */
fun creditLine(
    credit: Credit,
    unknownAuthor: String,
    unknownLicense: String,
): String = "${credit.author ?: unknownAuthor} · ${credit.license ?: unknownLicense}"
