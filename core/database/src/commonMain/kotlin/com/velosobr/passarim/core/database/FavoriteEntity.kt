// A tabela de favoritos e a conversão entre ela e o domínio.

package com.velosobr.passarim.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.velosobr.passarim.core.domain.SpeciesSummary

/**
 * Uma linha da tabela "favorites". @Entity diz ao Room que esta classe é uma
 * tabela, e @PrimaryKey marca a chave. Guardamos nome e miniatura (e não só o
 * id) para a lista de favoritos funcionar sem internet; addedAt é o momento
 * em que a ave foi favoritada, em milissegundos, e ordena a lista.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String?,
    val addedAt: Long,
)

/** Converte a linha do banco no resumo do domínio. */
internal fun FavoriteEntity.toSummary() = SpeciesSummary(id, commonName, scientificName, thumbnailUrl)

/** Converte o resumo do domínio numa linha nova, com o momento do favorito. */
internal fun SpeciesSummary.toEntity(addedAt: Long) = FavoriteEntity(id, commonName, scientificName, thumbnailUrl, addedAt)
