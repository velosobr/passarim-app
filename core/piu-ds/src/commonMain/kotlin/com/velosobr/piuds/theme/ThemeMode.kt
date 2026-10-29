// Decisão de usar ou não o tema escuro, a partir do que a pessoa escolheu
// nas Configurações. Mora no PiuDS porque o PiuTheme recebe o ThemeMode.

package com.velosobr.piuds.theme

import com.velosobr.passarim.core.domain.ThemeMode

/** Função de extensão: diz se o tema é escuro. SYSTEM segue o aparelho, LIGHT e DARK ignoram. */
fun ThemeMode.isDark(systemInDark: Boolean): Boolean =
    when (this) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
