// A "Ave do dia": a mesma ave o dia inteiro, para todo mundo, escolhida pelo
// catalog (frente B). O Explorar a mostra num card largo, em destaque.

package com.velosobr.passarim.core.domain

/**
 * A ave do dia. [date] é o dia dela no fuso de São Paulo ("2026-11-08"), [species] é o resumo de
 * sempre e [hasSong] diz se ela tem canto, para o card mostrar o botão de ouvir.
 */
data class FeaturedSpecies(
    val date: String,
    val species: SpeciesSummary,
    val hasSong: Boolean,
)

/**
 * Busca a ave do dia no BFF. Sem ave elegível, o BFF responde 404 e o resultado é
 * [DataError.Network.NotFound]; quem mostra o card some com ele em qualquer falha.
 */
interface FeaturedRepository {
    suspend fun getFeaturedSpecies(): Result<FeaturedSpecies, DataError.Network>
}
