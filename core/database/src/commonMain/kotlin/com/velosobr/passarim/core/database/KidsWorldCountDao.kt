// As consultas da tabela kids_world_counts, a última contagem de aves de cada
// mundo do Kids.

package com.velosobr.passarim.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** O DAO da contagem dos mundos; o Room gera a implementação a partir das anotações. */
@Dao
interface KidsWorldCountDao {
    /** Todas as contagens guardadas; emite de novo a cada mudança na tabela. */
    @Query("SELECT * FROM kids_world_counts")
    fun observeAll(): Flow<List<KidsWorldCountEntity>>

    /** Apaga todas as linhas. */
    @Query("DELETE FROM kids_world_counts")
    suspend fun deleteAll()

    /** Insere as linhas novas. */
    @Insert
    suspend fun insertAll(counts: List<KidsWorldCountEntity>)

    /**
     * Troca a contagem inteira: apaga a velha e insere a nova. @Transaction faz
     * as duas operações valerem como uma só, então quem observa nunca vê a
     * tabela vazia no meio da troca, nem uma mistura da velha com a nova.
     */
    @Transaction
    suspend fun replaceAll(counts: List<KidsWorldCountEntity>) {
        deleteAll()
        insertAll(counts)
    }
}
