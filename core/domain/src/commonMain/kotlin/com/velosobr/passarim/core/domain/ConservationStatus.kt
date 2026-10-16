// Status de conservação da IUCN e a conversão do código que vem do BFF.

package com.velosobr.passarim.core.domain

/**
 * As categorias da Lista Vermelha da IUCN, do menos ao mais ameaçado: LC
 * (pouco preocupante), NT, VU, EN, CR, EW (extinta na natureza), EX
 * (extinta) e DD (dados insuficientes).
 */
enum class ConservationStatus { LC, NT, VU, EN, CR, EW, EX, DD }

/** Converte o código do BFF; nulo ou desconhecido vira null (o app não mostra selo). */
fun conservationStatusOf(code: String?): ConservationStatus? = ConservationStatus.entries.firstOrNull { it.name == code }
