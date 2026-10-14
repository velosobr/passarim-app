package com.velosobr.passarim.feature.detail.domain

import com.velosobr.passarim.core.domain.ConservationStatus
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.core.domain.SpeciesSummary

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

data class Biome(
    val code: String,
    val label: String,
)

data class Photo(
    val thumbUrl: String?,
    val mediumUrl: String?,
    val largeUrl: String?,
    val credit: Credit,
) {
    val heroUrl: String? get() = largeUrl ?: mediumUrl
    val galleryUrl: String? get() = mediumUrl ?: largeUrl
}

data class Audio(
    val url: String,
    val durationMs: Long,
    val credit: Credit,
)

data class WhereToFind(
    val states: List<String>,
    val biomes: List<String>,
    val clusters: List<Cluster>,
)

data class Cluster(
    val lat: Double,
    val lng: Double,
    val count: Int,
)

fun SpeciesDetail.toSummary(): SpeciesSummary = SpeciesSummary(id, commonName, scientificName, photos.firstOrNull()?.thumbUrl)
