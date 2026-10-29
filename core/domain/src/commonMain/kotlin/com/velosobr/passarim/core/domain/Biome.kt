// Os seis biomas brasileiros, com o código que o BFF usa. No adulto, cada
// bioma tem um gradiente; no Kids, o bioma é o "mundo" da ave.

package com.velosobr.passarim.core.domain

/** Os biomas, na ordem oficial. [code] é o texto do BFF ("mata_atlantica"). */
enum class Biome(
    val code: String,
) {
    AMAZONIA("amazonia"),
    MATA_ATLANTICA("mata_atlantica"),
    CERRADO("cerrado"),
    CAATINGA("caatinga"),
    PANTANAL("pantanal"),
    PAMPA("pampa"),
}

/** Converte o código do BFF; nulo ou desconhecido vira null. */
fun biomeOf(code: String?): Biome? = Biome.entries.firstOrNull { it.code == code }
