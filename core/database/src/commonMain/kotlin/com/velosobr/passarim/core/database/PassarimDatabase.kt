// O banco do app, com a versão do esquema e a lista de tabelas.

package com.velosobr.passarim.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

/**
 * O banco Room. version é a versão do esquema: mudar uma tabela exige subir o
 * número e dizer como migrar. exportSchema grava o esquema de cada versão em
 * JSON (pasta schemas/), usado pelo teste de migração e pela migração automática.
 *
 * Versões:
 * - 1: a tabela de favoritos, com o id como chave.
 * - 2: os favoritos ganham audience (adulto ou Kids, com a chave virando o par
 *   id + audience) e primaryBiome, e nasce a tabela kids_world_counts.
 *
 * A @AutoMigration(from = 1, to = 2) faz o Room escrever a migração sozinho,
 * comparando o 1.json com o 2.json. O SQLite não troca a chave primária de uma
 * tabela que já existe; quando a chave muda, o Room cria uma tabela nova com o
 * formato da v2, copia as linhas (a coluna audience recebe o defaultValue
 * "ADULT" e o primaryBiome fica nulo), apaga a velha e renomeia a nova. Um SQL
 * escrito à mão poderia divergir do esquema e só quebrar no aparelho.
 */
@Database(
    entities = [FavoriteEntity::class, KidsWorldCountEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
@ConstructedBy(PassarimDatabaseConstructor::class)
abstract class PassarimDatabase : RoomDatabase() {
    /** O DAO dos favoritos; o Room gera a implementação. */
    abstract fun favoriteDao(): FavoriteDao
}

/**
 * O objeto que o Room usa para criar o banco (ligado pelo @ConstructedBy
 * acima). Fica sem implementação aqui: o Room (via KSP) gera o "actual" de
 * cada plataforma, e o @Suppress cala o erro de expect sem actual.
 */
@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object PassarimDatabaseConstructor : RoomDatabaseConstructor<PassarimDatabase> {
    override fun initialize(): PassarimDatabase
}

/** Nome do arquivo do banco, igual em todas as plataformas. */
internal const val DATABASE_FILE_NAME = "passarim.db"
