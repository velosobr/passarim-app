// Esquemas de cores claro e escuro da marca.

package com.velosobr.piuds.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Gerado a partir de docs/design/tokens/passarim-tokens.json (passarim-docs). Para mudar uma cor,
// edite o JSON, rode a verificação de contraste lá e atualize este arquivo (veja tokens/TOKENS.md).

/**
 * Esquema claro do Material 3. Cada papel tem a cor e o "on" (a cor do que
 * vai por cima): onPrimary é o texto sobre primary, e assim por diante.
 * Color(0xFF1A6D3C) é a cor em hexadecimal: FF de opacidade e depois RGB.
 */
val PiuLightColors =
    lightColorScheme(
        primary = Color(0xFF1A6D3C),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFA6F4B6),
        onPrimaryContainer = Color(0xFF00210E),
        secondary = Color(0xFF4E6352),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD0E8D2),
        onSecondaryContainer = Color(0xFF0B1F10),
        tertiary = Color(0xFF7A5900),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFDEA3),
        onTertiaryContainer = Color(0xFF261900),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFF6FBF3),
        onBackground = Color(0xFF181D18),
        surface = Color(0xFFF6FBF3),
        onSurface = Color(0xFF181D18),
        surfaceVariant = Color(0xFFDCE5DB),
        onSurfaceVariant = Color(0xFF414942),
        surfaceDim = Color(0xFFD7DBD4),
        surfaceBright = Color(0xFFF6FBF3),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF0F5ED),
        surfaceContainer = Color(0xFFEAF0E8),
        surfaceContainerHigh = Color(0xFFE4EAE2),
        surfaceContainerHighest = Color(0xFFDFE4DC),
        outline = Color(0xFF717971),
        outlineVariant = Color(0xFFC0C9BF),
        inverseSurface = Color(0xFF2D322D),
        inverseOnSurface = Color(0xFFEEF2EB),
        inversePrimary = Color(0xFF8BD89D),
        scrim = Color(0xFF000000),
    )

/** Esquema escuro do Material 3, com os mesmos papéis. */
val PiuDarkColors =
    darkColorScheme(
        primary = Color(0xFF7DDB97),
        onPrimary = Color(0xFF00391B),
        primaryContainer = Color(0xFF00522A),
        onPrimaryContainer = Color(0xFFA6F4B6),
        secondary = Color(0xFFB4CCB7),
        onSecondary = Color(0xFF203524),
        secondaryContainer = Color(0xFF364B3A),
        onSecondaryContainer = Color(0xFFD0E8D2),
        tertiary = Color(0xFFF0BF4C),
        onTertiary = Color(0xFF412D00),
        tertiaryContainer = Color(0xFF5E4200),
        onTertiaryContainer = Color(0xFFFFDEA3),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF101410),
        onBackground = Color(0xFFE0E4DC),
        surface = Color(0xFF101410),
        onSurface = Color(0xFFE0E4DC),
        surfaceVariant = Color(0xFF414942),
        onSurfaceVariant = Color(0xFFC0C9BF),
        surfaceDim = Color(0xFF101410),
        surfaceBright = Color(0xFF363A35),
        surfaceContainerLowest = Color(0xFF0B0F0B),
        surfaceContainerLow = Color(0xFF181D18),
        surfaceContainer = Color(0xFF1C211C),
        surfaceContainerHigh = Color(0xFF272B26),
        surfaceContainerHighest = Color(0xFF323630),
        outline = Color(0xFF97A097),
        outlineVariant = Color(0xFF414942),
        inverseSurface = Color(0xFFE0E4DC),
        inverseOnSurface = Color(0xFF2D322D),
        inversePrimary = Color(0xFF1A6D3C),
        scrim = Color(0xFF000000),
    )
