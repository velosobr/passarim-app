// Domínio compartilhado do app: o resumo de uma espécie, usado por várias
// features. O módulo core:domain é Kotlin puro (sem Android, sem iOS, sem
// rede), por isso compila para todas as plataformas e é fácil de testar.

package com.velosobr.passarim.core.domain

/**
 * Resumo de uma espécie, como aparece nos cards de Explorar e na lista de Favoritos.
 * [thumbnailUrl] é nulo quando a espécie ainda não tem foto (o card mostra o placeholder).
 *
 * "data class" é uma classe feita para guardar dados: o Kotlin gera sozinho
 * equals, hashCode, toString e copy a partir das propriedades. O "?" em
 * String? diz que o valor pode ser null; sem ele, o compilador proíbe null.
 */
data class SpeciesSummary(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String?,
)
