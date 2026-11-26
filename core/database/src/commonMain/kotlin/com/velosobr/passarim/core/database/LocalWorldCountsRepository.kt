// Implementação do WorldCountsRepository do domínio: busca a contagem dos
// mundos no BFF (pelo FiltersRepository, que o feature:explore:data registra no
// Koin) e a guarda no Room.

package com.velosobr.passarim.core.database

import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.EmptyResult
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.WorldCountsRepository
import com.velosobr.passarim.core.domain.asEmptyDataResult
import com.velosobr.passarim.core.domain.biomeOf
import com.velosobr.passarim.core.domain.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * A contagem vem do primaryBiomes de `GET /v1/filters?audience=kids` (o
 * getFilters(Journey.KIDS)). O FiltersRepository guarda a resposta em memória
 * enquanto o processo vive, então a contagem do banco muda, na prática, a cada
 * abertura do app, o que basta para um catálogo que muda pouco.
 *
 * - Um código de bioma que o app não conhece é ignorado (biomeOf devolve null).
 * - Se não sobra nenhum mundo conhecido, nada é gravado: uma lista vazia é um
 *   BFF que ignorou o audience, e a contagem boa que já estava fica.
 * - Uma falha da rede volta para quem chamou e também não mexe no banco.
 */
class LocalWorldCountsRepository(
    private val dao: KidsWorldCountDao,
    private val filters: FiltersRepository,
) : WorldCountsRepository {
    // mapNotNull descarta as linhas cujo código não vira um Biome (de uma
    // versão futura do app, por exemplo), e associate monta o mapa mundo → total.
    override fun observeWorldCounts(): Flow<Map<Biome, Int>> =
        dao
            .observeAll()
            .map { rows -> rows.mapNotNull { row -> biomeOf(row.biome)?.let { it to row.speciesCount } }.toMap() }
            .distinctUntilChanged()

    override suspend fun refreshWorldCounts(): EmptyResult<DataError.Network> =
        filters
            .getFilters(Journey.KIDS)
            .onSuccess { result ->
                val counts =
                    result.primaryBiomes.mapNotNull { filter ->
                        biomeOf(filter.code)?.let { KidsWorldCountEntity(it.code, filter.speciesCount) }
                    }
                if (counts.isNotEmpty()) dao.replaceAll(counts)
            }.asEmptyDataResult()
}
