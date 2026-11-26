// O contrato da contagem de aves de cada mundo do Kids, guardada no aparelho.
// Fica no core:domain porque quem lê (o Explorar Kids e o álbum) e quem
// implementa (o core:database, sobre o Room) são módulos diferentes, e as
// telas só conhecem esta interface.

package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

/**
 * Quantas aves o Kids tem em cada mundo (bioma principal), para o progresso
 * do álbum ("4 de 9") e dos cartões de mundo.
 *
 * A contagem vem do BFF e fica guardada, então o álbum mostra o progresso
 * mesmo sem internet, com a última contagem que chegou.
 */
interface WorldCountsRepository {
    /**
     * A última contagem guardada, por mundo; vazia enquanto nenhuma chegou.
     * É um Flow: emite de novo a cada [refreshWorldCounts] que grava.
     */
    fun observeWorldCounts(): Flow<Map<Biome, Int>>

    /**
     * Busca a contagem atual no BFF e troca a guardada. Uma falha (sem rede,
     * BFF fora do ar) volta como Failure e não apaga nada; uma lista vazia
     * também não apaga, porque quer dizer um BFF que não entendeu o Kids.
     * EmptyResult é o Result sem dado: só diz se deu certo ou qual erro houve.
     */
    suspend fun refreshWorldCounts(): EmptyResult<DataError.Network>
}
