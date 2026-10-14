package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.core.domain.Credit

const val SINGLE_COLUMN_FONT_SCALE = 1.5f
private const val MILLIS_PER_SECOND = 1_000
private const val SECONDS_PER_MINUTE = 60

fun factsInSingleColumn(fontScale: Float): Boolean = fontScale >= SINGLE_COLUMN_FONT_SCALE

fun formatTime(millis: Long): String {
    val totalSeconds = millis.coerceAtLeast(0) / MILLIS_PER_SECOND
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return "${totalSeconds / SECONDS_PER_MINUTE}:${seconds.toString().padStart(2, '0')}"
}

fun creditLine(
    credit: Credit,
    unknownAuthor: String,
    unknownLicense: String,
): String = "${credit.author ?: unknownAuthor} · ${credit.license ?: unknownLicense}"
