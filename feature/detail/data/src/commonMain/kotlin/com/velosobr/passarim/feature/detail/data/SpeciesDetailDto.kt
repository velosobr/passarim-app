package com.velosobr.passarim.feature.detail.data

import kotlinx.serialization.Serializable

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

@Serializable
internal data class ConservationStatusDto(
    val code: String,
)

@Serializable
internal data class CreditDto(
    val author: String,
    val license: String,
    val source: String,
    val sourceUrl: String,
)

@Serializable
internal data class FactDto(
    val text: String,
)

@Serializable
internal data class LabelDto(
    val code: String,
    val label: String,
)

@Serializable
internal data class PhotoDto(
    val thumbUrl: String? = null,
    val mediumUrl: String? = null,
    val largeUrl: String? = null,
    val credit: CreditDto,
)

@Serializable
internal data class AudioDto(
    val url: String,
    val durationMs: Long,
    val credit: CreditDto,
)

@Serializable
internal data class WhereToFindDto(
    val states: List<String> = emptyList(),
    val biomes: List<String> = emptyList(),
    val clusters: List<ClusterDto> = emptyList(),
)

@Serializable
internal data class ClusterDto(
    val lat: Double,
    val lng: Double,
    val count: Int,
)
