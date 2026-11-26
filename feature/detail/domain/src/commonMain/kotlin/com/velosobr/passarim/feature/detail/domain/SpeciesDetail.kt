// Os dados do Detalhe de uma espécie.
//
// Aqui existem dois "biomas": o Biome deste arquivo é o chip da tela (código e
// rótulo, como o BFF manda), e o com.velosobr.passarim.core.domain.Biome é o
// enum dos seis biomas, com gradiente no adulto. O bioma principal da ave usa
// o enum; para os dois nomes não se confundirem, ele entra como PrimaryBiome.

package com.velosobr.passarim.feature.detail.domain

import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.domain.Biome as PrimaryBiome

/**
 * A espécie completa como a tela de Detalhe usa. A primeira foto é a principal. [primaryBiome] é
 * o bioma principal da ave (o gradiente do card quando ela vira um resumo); fica nulo
 * com um BFF de antes da frente B ou com um bioma que o app não conhece.
 */
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
    val primaryBiome: PrimaryBiome? = null,
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

/**
 * O canto: URL, duração e crédito. [peaks] é a forma de onda já calculada pelo catalog: 64 alturas
 * de 0 (silêncio) a 1 (o trecho mais alto), da esquerda para a direita. Vazia quando o BFF não
 * manda (versão antiga ou canto ainda sem processar); aí a forma de onda vira uma linha reta.
 */
data class Audio(
    val url: String,
    val durationMs: Long,
    val credit: Credit,
    val peaks: List<Float> = emptyList(),
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

/** O resumo da espécie para guardar nos favoritos, com a miniatura da primeira foto e o bioma principal. */
fun SpeciesDetail.toSummary(): SpeciesSummary = SpeciesSummary(id, commonName, scientificName, photos.firstOrNull()?.thumbUrl, primaryBiome)
