// Esquema de cores do Piu Kids: uma variante só clara, com fundo creme e
// texto azul-noite. Gerado a partir do bloco "kids" de
// docs/design/tokens/passarim-tokens.json (passarim-docs, versão 2.0.0).
// Para mudar uma cor, edite o JSON, rode a verificação de contraste lá e
// atualize este arquivo (veja tokens/TOKENS.md, seção "Passarim Kids").

package com.velosobr.piuds.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Os mesmos papéis do Material 3, com as cores do Kids. O laranja vivo das
 * pranchetas é o primaryContainer (fundo de botão, com texto azul-noite); o
 * primary é um laranja escuro, que passa no contraste quando vira texto.
 */
val PiuKidsColors =
    lightColorScheme(
        primary = Color(0xFFA84300),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFF8A3D),
        onPrimaryContainer = Color(0xFF1D2B3A),
        secondary = Color(0xFF1E5FA6),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFCDEBFF),
        onSecondaryContainer = Color(0xFF0A2440),
        tertiary = Color(0xFFB8164F),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD6E0),
        onTertiaryContainer = Color(0xFF4A0A22),
        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFFFF7E8),
        onBackground = Color(0xFF1D2B3A),
        surface = Color(0xFFFFF7E8),
        onSurface = Color(0xFF1D2B3A),
        surfaceVariant = Color(0xFFF2E3C6),
        onSurfaceVariant = Color(0xFF4A5566),
        surfaceDim = Color(0xFFEADBBF),
        surfaceBright = Color(0xFFFFF7E8),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFFF2DC),
        surfaceContainer = Color(0xFFFCEDD3),
        surfaceContainerHigh = Color(0xFFF8E7C9),
        surfaceContainerHighest = Color(0xFFF2E3C6),
        outline = Color(0xFF6E7680),
        outlineVariant = Color(0xFFE2D2B2),
        inverseSurface = Color(0xFF1D2B3A),
        inverseOnSurface = Color(0xFFFFF7E8),
        inversePrimary = Color(0xFFFFC93C),
        scrim = Color(0xFF000000),
    )
