// O banco do app, com a versão do esquema e a lista de tabelas.

package com.velosobr.passarim.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

/**
 * O banco Room. version é a versão do esquema: mudar uma tabela exige subir o
 * número e escrever uma migração. exportSchema grava o esquema em JSON (pasta
 * schemas/), usado pelo teste de migração.
 */
@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = true)
@ConstructedBy(PassarimDatabaseConstructor::class)
abstract class PassarimDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}

/**
 * O construtor do banco. Fica sem implementação aqui: o Room gera o "actual"
 * de cada plataforma, e o @Suppress cala o aviso de que ele ainda não existe.
 */
@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object PassarimDatabaseConstructor : RoomDatabaseConstructor<PassarimDatabase> {
    override fun initialize(): PassarimDatabase
}

/** Nome do arquivo do banco, igual em todas as plataformas. */
internal const val DATABASE_FILE_NAME = "passarim.db"
