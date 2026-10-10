package com.velosobr.passarim.core.domain

/**
 * Resumo de uma espécie, como aparece nos cards de Explorar e na lista de Favoritos.
 * [thumbnailUrl] é nulo quando a espécie ainda não tem foto (o card mostra o placeholder).
 */
data class SpeciesSummary(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String?,
)
