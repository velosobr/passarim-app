// As consultas da tabela de favoritos.

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
 * mudança na tabela.
 */
@Dao
interface FavoriteDao {
    /** Os favoritos, do mais recente para o mais antigo. */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    /** Só os ids dos favoritos. */
    @Query("SELECT id FROM favorites")
    fun observeIds(): Flow<List<String>>

    /** Insere um favorito. */
    @Insert
    suspend fun insert(favorite: FavoriteEntity)

    /** Apaga um favorito e devolve quantas linhas apagou (0 se não era favorito). */
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String): Int

    /** Atualiza nome e miniatura de um favorito; sem efeito se a ave não for favorita. */
    @Query("UPDATE favorites SET commonName = :commonName, scientificName = :scientificName, thumbnailUrl = :thumbnailUrl WHERE id = :id")
    suspend fun updateSummary(
        id: String,
        commonName: String,
        scientificName: String,
        thumbnailUrl: String?,
    )

    /**
     * Favorita ou desfavorita: se apagou alguma linha, a ave era favorita; se não,
     * insere. @Transaction faz as duas operações valerem como uma só.
     */
    @Transaction
    suspend fun toggle(favorite: FavoriteEntity) {
        if (deleteById(favorite.id) == 0) insert(favorite)
    }
}
