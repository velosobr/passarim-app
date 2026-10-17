// Os dados do Detalhe de uma espécie.

package com.velosobr.passarim.feature.detail.domain

import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.core.domain.SpeciesSummary

/** A espécie completa como a tela de Detalhe usa. A primeira foto é a principal. */
data class SpeciesDetail(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val family: String,
    val conservation: ConservationStatus?,
    val sizeCm: Int?,
    val diet: String?,
    val description: String,
    val descriptionCredit: Credit,
    val facts: List<String>,
    val biomes: List<Biome>,
    val photos: List<Photo>,
    val audio: Audio?,
    val whereToFind: WhereToFind,
)

/** Um bioma com o rótulo para o chip. */
data class Biome(
    val code: String,
    val label: String,
)

/**
 * Uma foto nos três tamanhos. [heroUrl] prefere a grande (para o topo) e
 * [galleryUrl] a média (para as miniaturas), cada uma caindo na outra se faltar.
 */
data class Photo(
    val thumbUrl: String?,
    val mediumUrl: String?,
    val largeUrl: String?,
    val credit: Credit,
) {
    val heroUrl: String? get() = largeUrl ?: mediumUrl
    val galleryUrl: String? get() = mediumUrl ?: largeUrl
}

/** O canto: URL, duração e crédito. */
data class Audio(
    val url: String,
    val durationMs: Long,
    val credit: Credit,
)

/** A seção "Onde encontrar": UFs, biomas e grupos de avistamentos. */
data class WhereToFind(
    val states: List<String>,
    val biomes: List<String>,
    val clusters: List<Cluster>,
)

/** Um grupo de avistamentos no mapa. */
data class Cluster(
    val lat: Double,
    val lng: Double,
    val count: Int,
)

/** O resumo da espécie para guardar nos favoritos, com a miniatura da primeira foto. */
fun SpeciesDetail.toSummary(): SpeciesSummary = SpeciesSummary(id, commonName, scientificName, photos.firstOrNull()?.thumbUrl)
