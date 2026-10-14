package com.velosobr.passarim.app.favorites

import com.velosobr.passarim.core.domain.FavoritesRepository
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Favoritos em memória (somem ao fechar o app). Provisório: a 5d troca esta classe por uma com Room
 * no módulo Koin do `app`, e as features continuam vendo só a interface [FavoritesRepository].
 */
class InMemoryFavoritesRepository : FavoritesRepository {
    private val ids = MutableStateFlow<Set<String>>(emptySet())

    override fun observeIds(): Flow<Set<String>> = ids.asStateFlow()

    override suspend fun toggle(summary: SpeciesSummary) {
        ids.update { if (summary.id in it) it - summary.id else it + summary.id }
    }
}
