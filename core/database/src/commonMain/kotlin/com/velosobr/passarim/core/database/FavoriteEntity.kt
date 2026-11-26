// A tabela de favoritos e a conversão entre ela e o domínio. A mesma tabela
// guarda os favoritos do adulto e as figurinhas do álbum do Kids, separados
// pela coluna audience.

package com.velosobr.passarim.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.SpeciesSummary
import com.velosobr.passarim.core.domain.biomeOf

/**
 * Uma linha da tabela "favorites". @Entity diz ao Room que esta classe é uma
 * tabela. Guardamos nome e URL da miniatura (e não só o id) para a lista abrir
 * sem chamar o BFF (a foto vem do cache de imagens); addedAt é o momento em
 * que a ave foi guardada, em milissegundos, e ordena a lista.
 *
 * - [audience] é o nome da Journey ("ADULT" ou "KIDS"): de quem é o favorito.
 *   O álbum do Kids é o favorito com audience = "KIDS". O defaultValue entra
 *   no SQL da coluna, e é ele que põe "ADULT" nas linhas que já existiam
 *   quando a migração para a versão 2 criou a coluna.
 * - A chave é o par (id, audience), declarado em primaryKeys: a mesma ave
 *   pode estar nos favoritos do adulto e no álbum da criança, cada uma com a
 *   própria data, mas não duas vezes na mesma audiência.
 * - [primaryBiome] é o código do bioma principal ("pantanal"), o "mundo" da
 *   figurinha no álbum. Nulo nos favoritos antigos e quando o BFF não manda.
 */
@Entity(tableName = "favorites", primaryKeys = ["id", "audience"])
data class FavoriteEntity(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val thumbnailUrl: String?,
    val addedAt: Long,
    @ColumnInfo(defaultValue = "ADULT") val audience: String,
    val primaryBiome: String?,
)

/** Converte a linha do banco no resumo do domínio; um bioma que o app não conhece vira null. */
internal fun FavoriteEntity.toSummary() = SpeciesSummary(id, commonName, scientificName, thumbnailUrl, biomeOf(primaryBiome))

/** Converte o resumo do domínio numa linha nova da [audience], com o momento em que foi guardado. */
internal fun SpeciesSummary.toEntity(
    addedAt: Long,
    audience: Journey,
) = FavoriteEntity(id, commonName, scientificName, thumbnailUrl, addedAt, audience.name, primaryBiome?.code)
