// Funções puras que o PiuTheme usa para decidir o esquema de cores e se as
// animações ficam ligadas. Ficam fora do @Composable para serem testadas
// sem Compose, em commonTest.

package com.velosobr.piuds.theme

import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.ThemeMode

/**
 * O esquema que o tema vai aplicar: a variante (pela jornada), se é escuro
 * e se usa as cores dinâmicas do sistema (o papel de parede, no Android 12+).
 */
data class PiuSchemeChoice(
    val journey: Journey,
    val dark: Boolean,
    val dynamic: Boolean,
)

/**
 * O Kids é sempre claro e sem cores dinâmicas, qualquer que seja a
 * preferência do adulto (que não é alterada: o Kids só a ignora). O adulto
 * segue o [themeMode] e o pedido de [dynamicColor].
 */
fun resolvePiuScheme(
    journey: Journey,
    themeMode: ThemeMode,
    systemDark: Boolean,
    dynamicColor: Boolean,
): PiuSchemeChoice =
    when (journey) {
        Journey.KIDS -> PiuSchemeChoice(journey, dark = false, dynamic = false)
        Journey.ADULT -> PiuSchemeChoice(journey, dark = themeMode.isDark(systemDark), dynamic = dynamicColor)
    }

/**
 * Um valor fixado de fora (testes e snapshots) ganha; sem ele, lê a
 * preferência do sistema. [system] é uma função para só ser chamada quando
 * precisa (ler o sistema tem custo).
 */
fun resolveReduceMotion(
    override: Boolean?,
    system: () -> Boolean,
): Boolean = override ?: system()
