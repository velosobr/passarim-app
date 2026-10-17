// Status de conservação da IUCN e a conversão do código que vem do BFF.
// "enum class" é um tipo com uma lista fechada de valores; entries é a lista
// deles, na ordem da declaração.

package com.velosobr.passarim.core.domain

/**
 * As categorias da Lista Vermelha da IUCN. De LC a EX, do menos ao mais
 * ameaçado: LC (pouco preocupante), NT, VU, EN, CR, EW (extinta na natureza)
 * e EX (extinta). DD (dados insuficientes) fica à parte.
 */
enum class ConservationStatus { LC, NT, VU, EN, CR, EW, EX, DD }

/** Converte o código do BFF; nulo ou desconhecido vira null (o app não mostra selo). */
fun conservationStatusOf(code: String?): ConservationStatus? = ConservationStatus.entries.firstOrNull { it.name == code }
