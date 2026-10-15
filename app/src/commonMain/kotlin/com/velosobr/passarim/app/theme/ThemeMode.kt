package com.velosobr.passarim.app.theme

import com.velosobr.passarim.core.domain.ThemeMode

fun ThemeMode.isDark(systemInDark: Boolean): Boolean =
    when (this) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
