// Os DTOs da resposta de `GET /v1/species/{id}`, no formato do contrato do BFF.
// Campos com valor padrão (= null, = emptyList()) podem faltar no JSON sem
// quebrar a leitura.

package com.velosobr.passarim.feature.detail.data

import kotlinx.serialization.Serializable

/** A espécie completa; os campos que o app não usa (como a fonte de cada fato) ficam de fora. */
@Serializable
internal data class SpeciesDetailDto(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val family: String,
    val conservationStatus: ConservationStatusDto? = null,
    val sizeCm: Int? = null,
    val diet: String? = null,
    val description: String,
    val descriptionCredit: CreditDto,
    val facts: List<FactDto> = emptyList(),
    val chips: List<LabelDto> = emptyList(),
    val photos: List<PhotoDto> = emptyList(),
    val audio: AudioDto? = null,
    val whereToFind: WhereToFindDto,
)

/** O status de conservação; o app só lê o código e usa o próprio rótulo. */
@Serializable
internal data class ConservationStatusDto(
    val code: String,
)

/** Crédito de autoria. */
@Serializable
internal data class CreditDto(
    val author: String,
    val license: String,
    val source: String,
    val sourceUrl: String,
)

/** Uma curiosidade. */
@Serializable
internal data class FactDto(
    val text: String,
)

/** Um chip de bioma: código e rótulo. */
@Serializable
internal data class LabelDto(
    val code: String,
    val label: String,
)

/** Uma foto nos três tamanhos. */
@Serializable
internal data class PhotoDto(
    val thumbUrl: String? = null,
    val mediumUrl: String? = null,
    val largeUrl: String? = null,
    val credit: CreditDto,
)

/** O canto. */
@Serializable
internal data class AudioDto(
    val url: String,
    val durationMs: Long,
    val credit: CreditDto,
)

/** A seção "Onde encontrar". */
@Serializable
internal data class WhereToFindDto(
    val states: List<String> = emptyList(),
    val biomes: List<String> = emptyList(),
    val clusters: List<ClusterDto> = emptyList(),
)

/** Um grupo de avistamentos do mapa. */
@Serializable
internal data class ClusterDto(
    val lat: Double,
    val lng: Double,
    val count: Int,
)
