package com.velosobr.passarim.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT id FROM favorites")
    fun observeIds(): Flow<List<String>>

    @Insert
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("UPDATE favorites SET commonName = :commonName, scientificName = :scientificName, thumbnailUrl = :thumbnailUrl WHERE id = :id")
    suspend fun updateSummary(
        id: String,
        commonName: String,
        scientificName: String,
        thumbnailUrl: String?,
    )

    @Transaction
    suspend fun toggle(favorite: FavoriteEntity) {
        if (deleteById(favorite.id) == 0) insert(favorite)
    }
}
