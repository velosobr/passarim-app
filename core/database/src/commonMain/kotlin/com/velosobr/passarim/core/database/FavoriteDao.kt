// As consultas da tabela de favoritos. Toda consulta filtra pela audiência
// (ADULT ou KIDS), para os favoritos do adulto e o álbum do Kids nunca se
// misturarem.

package com.velosobr.passarim.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) é onde ficam as consultas. O Room gera a
 * implementação a partir das anotações: @Query com o SQL, @Insert para
 * inserir. Funções que devolvem Flow são "vivas": emitem de novo a cada
 * mudança na tabela. Nos SQLs, ":audience" é o parâmetro da função com o
 * mesmo nome (o nome da Journey, "ADULT" ou "KIDS").
 */
@Dao
interface FavoriteDao {
    /** Os favoritos da audiência, do mais recente para o mais antigo. */
    @Query("SELECT * FROM favorites WHERE audience = :audience ORDER BY addedAt DESC")
    fun observeAll(audience: String): Flow<List<FavoriteEntity>>

    /** Só os ids dos favoritos da audiência. */
    @Query("SELECT id FROM favorites WHERE audience = :audience")
    fun observeIds(audience: String): Flow<List<String>>

    /** Insere um favorito. */
    @Insert
    suspend fun insert(favorite: FavoriteEntity)

    /** Apaga um favorito da audiência e devolve quantas linhas apagou (0 se não estava lá). */
    @Query("DELETE FROM favorites WHERE id = :id AND audience = :audience")
    suspend fun deleteById(
        id: String,
        audience: String,
    ): Int

    /** Atualiza nome, miniatura e bioma de um favorito da audiência; sem efeito se a ave não estiver lá. */
    @Query(
        "UPDATE favorites SET commonName = :commonName, scientificName = :scientificName, thumbnailUrl = :thumbnailUrl, " +
            "primaryBiome = :primaryBiome WHERE id = :id AND audience = :audience",
    )
    suspend fun updateSummary(
        id: String,
        audience: String,
        commonName: String,
        scientificName: String,
        thumbnailUrl: String?,
        primaryBiome: String?,
    )

    /**
     * Guarda ou tira: se apagou alguma linha da mesma audiência, a ave estava
     * lá; se não, insere. @Transaction faz as duas operações valerem como uma só.
     */
    @Transaction
    suspend fun toggle(favorite: FavoriteEntity) {
        if (deleteById(favorite.id, favorite.audience) == 0) insert(favorite)
    }
}
