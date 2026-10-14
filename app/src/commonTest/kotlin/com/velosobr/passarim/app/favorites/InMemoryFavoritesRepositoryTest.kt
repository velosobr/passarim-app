package com.velosobr.passarim.app.favorites

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InMemoryFavoritesRepositoryTest {
    private val bemTeVi = SpeciesSummary("bem-te-vi", "Bem-te-vi", "Pitangus sulphuratus", null)
    private val sabia = SpeciesSummary("sabia-laranjeira", "Sabiá-laranjeira", "Turdus rufiventris", null)

    @Test
    fun `comeca vazio e cada toggle emite o conjunto novo`() =
        runTest {
            val repo = InMemoryFavoritesRepository()
            repo.observeIds().test {
                assertThat(awaitItem()).isEmpty()
                repo.toggle(bemTeVi)
                assertThat(awaitItem()).isEqualTo(setOf("bem-te-vi"))
                repo.toggle(sabia)
                assertThat(awaitItem()).isEqualTo(setOf("bem-te-vi", "sabia-laranjeira"))
                repo.toggle(bemTeVi)
                assertThat(awaitItem()).isEqualTo(setOf("sabia-laranjeira"))
            }
        }

    @Test
    fun `refresh atualiza o resumo de um favorito e ignora quem nao e favorito`() =
        runTest {
            val repo = InMemoryFavoritesRepository()
            repo.toggle(bemTeVi)
            val novaFoto = bemTeVi.copy(thumbnailUrl = "https://media/bem-te-vi.webp")

            repo.refresh(novaFoto)
            repo.refresh(sabia)

            assertThat(repo.summaries.value).isEqualTo(mapOf("bem-te-vi" to novaFoto))
        }
}
