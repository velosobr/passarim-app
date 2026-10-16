// Contrato dos favoritos. A implementação (Room, no core:database) fica fora
// do domínio; as features só conhecem esta interface.

package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

/**
 * Favoritos guardados no aparelho, que funcionam sem internet.
 *
 * Flow é um fluxo de valores ao longo do tempo: quem observa recebe o valor
 * atual e, depois, cada mudança. Funções "suspend" podem pausar sem travar a
 * tela (rodam numa coroutine) e só podem ser chamadas de dentro de outra
 * função suspend ou de uma coroutine.
 */
interface FavoritesRepository {
    /** Os ids dos favoritos, para marcar o coração nos cards. */
    fun observeIds(): Flow<Set<String>>

    /** Os favoritos completos, do mais recente para o mais antigo. */
    fun observeAll(): Flow<List<SpeciesSummary>>

    /** Favorita a espécie, ou desfavorita se ela já for favorita. */
    suspend fun toggle(summary: SpeciesSummary)

    /** Atualiza nome e miniatura de uma espécie que já é favorita (ao abrir o Detalhe); não favorita ninguém. */
    suspend fun refresh(summary: SpeciesSummary)
}
