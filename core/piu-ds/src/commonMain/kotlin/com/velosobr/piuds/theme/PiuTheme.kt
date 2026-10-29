// O tema do PiuDS, que todas as telas usam por fora. Escolhe a variante pela
// jornada: Piu (adulto, claro, escuro e cores dinâmicas) ou Piu Kids (sempre
// claro). CompositionLocalProvider entrega os tokens extras e "reduzir
// animações" a todos os componentes de dentro.

package com.velosobr.piuds.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.ThemeMode

/** Se o que está na tela é escuro (no Kids, nunca). */
private val LocalPiuIsDark = staticCompositionLocalOf { false }

/** "Reduzir animações" já resolvido (o valor fixado de fora ou o do sistema). */
private val LocalPiuReduceMotionResolved = staticCompositionLocalOf { false }

/**
 * Aplica a variante da [journey]. No adulto: as cores dinâmicas (se pedidas
 * e disponíveis) ou as da marca, no claro ou escuro de [themeMode]. No Kids:
 * sempre o esquema claro do Kids, ignorando [themeMode] e [dynamicColor] sem
 * gravar nada (as preferências do adulto não mudam).
 */
@Composable
fun PiuTheme(
    journey: Journey = Journey.ADULT,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // isSystemInDarkTheme() diz se o aparelho está no modo escuro (o "Sistema" das Configurações).
    val choice = resolvePiuScheme(journey, themeMode, isSystemInDarkTheme(), dynamicColor)
    // Testes e snapshots fixam o valor; sem isso, vale a preferência do sistema.
    val reduceMotion = rememberReduceMotion(LocalPiuReduceMotion.current)
    val kids = choice.journey == Journey.KIDS
    val colors =
        when {
            kids -> PiuKidsColors
            choice.dynamic -> dynamicColorScheme(choice.dark) ?: brandColors(choice.dark)
            else -> brandColors(choice.dark)
        }
    val tokens = if (kids) PiuKidsTokens else piuAdultTokens(choice.dark)
    CompositionLocalProvider(
        LocalPiuTokens provides tokens,
        LocalPiuIsDark provides choice.dark,
        LocalPiuReduceMotionResolved provides reduceMotion,
    ) {
        // TODO(frente-H): adotar MaterialExpressiveTheme e MotionScheme.expressive() quando saírem em versão
        // estável do CMP material3 (no 1.9.0 eles são internos). Até lá, as molas ficam com os componentes (PiuMotion, plano C2).
        MaterialTheme(
            colorScheme = colors,
            shapes = if (kids) PiuKidsShapes else PiuShapes,
            typography = if (kids) piuKidsTypography() else piuTypography(),
            content = content,
        )
    }
}

private fun brandColors(dark: Boolean) = if (dark) PiuDarkColors else PiuLightColors

/**
 * O que o Material não tem, lido de dentro do tema: PiuTheme.tokens,
 * PiuTheme.reduceMotion e PiuTheme.isDark. Cores, tipografia e formas
 * continuam no MaterialTheme. "@ReadOnlyComposable" avisa o Compose que o
 * getter só lê valores da composição (não desenha nada), o que o deixa mais
 * barato de chamar.
 */
object PiuTheme {
    val tokens: PiuTokens
        @Composable @ReadOnlyComposable
        get() = LocalPiuTokens.current

    val reduceMotion: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalPiuReduceMotionResolved.current

    val isDark: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalPiuIsDark.current
}
