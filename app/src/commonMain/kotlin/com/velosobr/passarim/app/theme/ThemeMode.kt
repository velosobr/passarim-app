// Decisão de usar ou não o tema escuro.

package com.velosobr.passarim.app.theme

import com.velosobr.passarim.core.domain.ThemeMode

/** Diz se o tema é escuro: SYSTEM segue o aparelho, LIGHT e DARK ignoram. */
fun ThemeMode.isDark(systemInDark: Boolean): Boolean =
    when (this) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
