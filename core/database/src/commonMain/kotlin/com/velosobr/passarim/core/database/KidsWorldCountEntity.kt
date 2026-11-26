// A tabela com a última contagem de aves de cada mundo do Kids, guardada para
// o álbum mostrar o progresso ("4 de 9") mesmo sem internet.

package com.velosobr.passarim.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Uma linha da tabela "kids_world_counts": quantas aves o Kids tem num mundo.
 * [biome] é o código do bioma ("cerrado"), a chave, então há no máximo uma
 * linha por mundo. A contagem vem do `GET /v1/filters?audience=kids`
 * (o campo primaryBiomes) e é trocada inteira a cada atualização.
 */
@Entity(tableName = "kids_world_counts")
data class KidsWorldCountEntity(
    @PrimaryKey val biome: String,
    val speciesCount: Int,
)
