// Testes do repositório de favoritos com um banco Room de verdade, só que em
// memória (some no fim do teste). Turbine testa Flows: "test { awaitItem() }"
// espera o próximo valor emitido. Os testes do adulto usam ADULT; os do fim
// conferem que o álbum do Kids (KIDS) e os favoritos do adulto não se misturam.

package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.Journey.ADULT
import com.velosobr.passarim.core.domain.Journey.KIDS
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

/**
 * O relógio é um contador ({ ++now }): cada favorito ganha um momento maior
 * que o anterior, e a ordem da lista fica previsível.
 */
class LocalFavoritesRepositoryTest {
    private val bemTeVi = SpeciesSummary("bem-te-vi", "Bem-te-vi", "Pitangus sulphuratus", "https://media/bem-te-vi.webp")
    private val sabia = SpeciesSummary("sabia-laranjeira", "Sabiá-laranjeira", "Turdus rufiventris", null)
    private val tucano = SpeciesSummary("tucano-toco", "Tucano-toco", "Ramphastos toco", null)
    private val tuiuiu = SpeciesSummary("jabiru-mycteria", "Tuiuiú", "Jabiru mycteria", null, Biome.PANTANAL)

    private val database =
        Room
            .inMemoryDatabaseBuilder<PassarimDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    private var now = 0L
    private val repository = LocalFavoritesRepository(database.favoriteDao()) { ++now }

    /** Fecha o banco depois de cada teste. */
    @AfterTest
    fun closeDatabase() = database.close()

    @Test
    fun `comeca vazio`() =
        runTest {
            assertThat(repository.observeAll(ADULT).first()).isEmpty()
            assertThat(repository.observeIds(ADULT).first()).isEmpty()
        }

    @Test
    fun `toggle grava e lista do mais recente para o mais antigo`() =
        runTest {
            repository.toggle(bemTeVi, ADULT)
            repository.toggle(sabia, ADULT)
            repository.toggle(tucano, ADULT)

            assertThat(repository.observeAll(ADULT).first()).containsExactly(tucano, sabia, bemTeVi)
        }

    @Test
    fun `toggle de um favorito remove`() =
        runTest {
            repository.toggle(bemTeVi, ADULT)
            repository.toggle(sabia, ADULT)

            repository.toggle(bemTeVi, ADULT)

            assertThat(repository.observeAll(ADULT).first()).containsExactly(sabia)
        }

    @Test
    fun `observeIds emite a cada mudanca`() =
        runTest {
            repository.observeIds(ADULT).test {
                assertThat(awaitItem()).isEmpty()
                repository.toggle(bemTeVi, ADULT)
                assertThat(awaitItem()).isEqualTo(setOf("bem-te-vi"))
                repository.toggle(sabia, ADULT)
                assertThat(awaitItem()).isEqualTo(setOf("bem-te-vi", "sabia-laranjeira"))
                repository.toggle(bemTeVi, ADULT)
                assertThat(awaitItem()).isEqualTo(setOf("sabia-laranjeira"))
            }
        }

    @Test
    fun `toggles simultaneos em numero par nao deixam rastro`() =
        runTest {
            List(10) { async(Dispatchers.IO) { repository.toggle(bemTeVi, ADULT) } }.awaitAll()

            assertThat(repository.observeAll(ADULT).first()).isEmpty()
        }

    @Test
    fun `toggles simultaneos em numero impar deixam um registro so`() =
        runTest {
            List(11) { async(Dispatchers.IO) { repository.toggle(bemTeVi, ADULT) } }.awaitAll()

            assertThat(repository.observeAll(ADULT).first()).containsExactly(bemTeVi)
        }

    @Test
    fun `refresh atualiza o resumo e mantem a posicao na lista`() =
        runTest {
            repository.toggle(bemTeVi, ADULT)
            repository.toggle(sabia, ADULT)
            val novaFoto = bemTeVi.copy(thumbnailUrl = "https://media/bem-te-vi-2.webp")

            repository.refresh(novaFoto, ADULT)

            assertThat(repository.observeAll(ADULT).first()).containsExactly(sabia, novaFoto)
        }

    @Test
    fun `refresh ignora quem nao e favorito`() =
        runTest {
            repository.refresh(tucano, ADULT)

            assertThat(repository.observeAll(ADULT).first()).isEmpty()
        }

    @Test
    fun `a mesma ave convive no adulto e no Kids`() =
        runTest {
            repository.toggle(tuiuiu, ADULT)
            repository.toggle(tuiuiu, KIDS)

            assertThat(repository.observeAll(ADULT).first()).containsExactly(tuiuiu)
            assertThat(repository.observeAll(KIDS).first()).containsExactly(tuiuiu)
        }

    @Test
    fun `toggle numa audiencia nao mexe na outra`() =
        runTest {
            repository.toggle(tuiuiu, ADULT)
            repository.toggle(bemTeVi, KIDS)

            repository.toggle(tuiuiu, KIDS)
            repository.toggle(tuiuiu, KIDS)

            assertThat(repository.observeAll(ADULT).first()).containsExactly(tuiuiu)
            assertThat(repository.observeIds(KIDS).first()).isEqualTo(setOf("bem-te-vi"))
        }

    @Test
    fun `observeAll do Kids devolve o bioma principal`() =
        runTest {
            repository.toggle(tuiuiu, KIDS)

            assertThat(
                repository
                    .observeAll(KIDS)
                    .first()
                    .single()
                    .primaryBiome,
            ).isEqualTo(Biome.PANTANAL)
        }

    @Test
    fun `refresh atualiza o bioma principal so na audiencia pedida`() =
        runTest {
            val semBioma = tuiuiu.copy(primaryBiome = null)
            repository.toggle(semBioma, ADULT)
            repository.toggle(semBioma, KIDS)

            repository.refresh(tuiuiu, KIDS)

            assertThat(repository.observeAll(KIDS).first()).containsExactly(tuiuiu)
            assertThat(repository.observeAll(ADULT).first()).containsExactly(semBioma)
        }
}
