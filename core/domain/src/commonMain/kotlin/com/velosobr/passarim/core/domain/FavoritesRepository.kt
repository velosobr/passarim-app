// Contrato dos favoritos. A implementação (Room, no core:database) fica fora
// do domínio; as features só conhecem esta interface.

package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

/**
 * Favoritos guardados no aparelho, que funcionam sem internet.
 *
 * Flow é um fluxo de valores ao longo do tempo. Aqui, o Room emite a lista
 * atual assim que alguém começa a observar e de novo a cada mudança. Funções "suspend" podem pausar sem travar a
 * tela (rodam numa coroutine) e só podem ser chamadas de dentro de outra
 * função suspend ou de uma coroutine.
 *
 * Todo método recebe a audiência, a [Journey] de quem guardou: ADULT são os
 * favoritos do Passarim, KIDS é o álbum de figurinhas do Kids. As duas nunca se
 * misturam: a mesma ave pode estar nas duas, cada uma com a própria data, e
 * mexer numa não muda a outra. O parâmetro é obrigatório (sem valor padrão)
 * para nenhuma tela esquecer de dizer de quem está falando.
 */
interface FavoritesRepository {
    /** Os ids dos favoritos da [audience], para marcar o coração (ou o selo de figurinha) nos cards. */
    fun observeIds(audience: Journey): Flow<Set<String>>

    /** Os favoritos completos da [audience], do mais recente para o mais antigo, com o bioma principal de cada um. */
    fun observeAll(audience: Journey): Flow<List<SpeciesSummary>>

    /** Guarda a espécie na [audience] (com o bioma principal dela), ou tira se ela já estiver lá. */
    suspend fun toggle(
        summary: SpeciesSummary,
        audience: Journey,
    )

    /**
     * Atualiza nome, miniatura e bioma principal de uma espécie que já está na [audience] (ao abrir o
     * Detalhe); não guarda ninguém novo.
     */
    suspend fun refresh(
        summary: SpeciesSummary,
        audience: Journey,
    )
}
