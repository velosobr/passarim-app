// As alturas do card do mosaico do Explorar adulto (PiuMosaicCard). A grade
// da frente E alterna as duas para formar o mosaico.

package com.velosobr.piuds.components.adult

/**
 * As duas alturas do card no mosaico (prancheta Explorar: 220 e 168 dp).
 * enum class é um tipo com um conjunto fechado de valores: quem chama só
 * pode escolher TALL ou SHORT, nunca uma altura qualquer.
 */
enum class PiuMosaicHeight(
    internal val minHeightDp: Int,
) {
    TALL(220),
    SHORT(168),
}
