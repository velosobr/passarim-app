package com.velosobr.passarim.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.velosobr.passarim.core.domain.SpeciesSummary

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String?,
    val addedAt: Long,
)

internal fun FavoriteEntity.toSummary() = SpeciesSummary(id, commonName, scientificName, thumbnailUrl)

internal fun SpeciesSummary.toEntity(addedAt: Long) = FavoriteEntity(id, commonName, scientificName, thumbnailUrl, addedAt)
