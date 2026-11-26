// Testes da contagem de aves por mundo do Kids, com um banco Room em memória
// e um FiltersRepository falso no lugar do BFF.

package com.velosobr.passarim.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.domain.BiomeFilter
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Filters
import com.velosobr.passarim.core.domain.FiltersRepository
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.WorldCountsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test

/** Filtros falsos: respondem [next] e guardam a audiência de cada pedido. */
private class FakeFiltersRepository : FiltersRepository {
    var next: Result<Filters, DataError.Network> = Result.Success(kidsFilters(BiomeFilter("cerrado", "Cerrado", 11)))
    val audiences = mutableListOf<Journey>()

    override suspend fun getFilters(audience: Journey): Result<Filters, DataError.Network> {
        audiences += audience
        return next
    }
}

/** Os filtros do Kids com só a contagem por bioma principal, que é o que a contagem dos mundos lê. */
private fun kidsFilters(vararg primaryBiomes: BiomeFilter) =
    Filters(biomes = emptyList(), states = emptyList(), primaryBiomes = primaryBiomes.toList())

class LocalWorldCountsRepositoryTest {
    private val database =
        Room
            .inMemoryDatabaseBuilder<PassarimDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    private val filters = FakeFiltersRepository()
    private val repository = LocalWorldCountsRepository(database.kidsWorldCountDao(), filters)

    @AfterTest
    fun closeDatabase() = database.close()

    @Test
    fun `comeca sem contagem`() =
        runTest {
            assertThat(repository.observeWorldCounts().first()).isEmpty()
        }

    @Test
    fun `refresh pede os filtros do Kids e grava a contagem de cada mundo`() =
        runTest {
            filters.next =
                Result.Success(
                    kidsFilters(
                        BiomeFilter("amazonia", "Amazônia", 9),
                        BiomeFilter("caatinga", "Caatinga", 2),
                        BiomeFilter("pampa", "Pampa", 0),
                    ),
                )

            val result = repository.refreshWorldCounts()

            assertThat(result).isEqualTo(Result.Success(Unit))
            assertThat(filters.audiences).isEqualTo(listOf(Journey.KIDS))
            assertThat(repository.observeWorldCounts().first())
                .isEqualTo(mapOf(Biome.AMAZONIA to 9, Biome.CAATINGA to 2, Biome.PAMPA to 0))
        }

    @Test
    fun `refresh ignora bioma desconhecido`() =
        runTest {
            filters.next = Result.Success(kidsFilters(BiomeFilter("cerrado", "Cerrado", 11), BiomeFilter("marte", "Marte", 3)))

            repository.refreshWorldCounts()

            assertThat(repository.observeWorldCounts().first()).isEqualTo(mapOf(Biome.CERRADO to 11))
        }

    @Test
    fun `refresh troca todas as linhas de uma vez`() =
        runTest {
            filters.next = Result.Success(kidsFilters(BiomeFilter("cerrado", "Cerrado", 11), BiomeFilter("pantanal", "Pantanal", 5)))
            repository.refreshWorldCounts()

            filters.next = Result.Success(kidsFilters(BiomeFilter("cerrado", "Cerrado", 12)))
            repository.refreshWorldCounts()

            assertThat(repository.observeWorldCounts().first()).isEqualTo(mapOf(Biome.CERRADO to 12))
        }

    @Test
    fun `com falha a contagem guardada fica e o erro volta`() =
        runTest {
            repository.refreshWorldCounts()
            filters.next = Result.Failure(DataError.Network.NoConnection)

            val result = repository.refreshWorldCounts()

            assertThat(result).isEqualTo(Result.Failure(DataError.Network.NoConnection))
            assertThat(repository.observeWorldCounts().first()).isEqualTo(mapOf(Biome.CERRADO to 11))
        }

    @Test
    fun `uma lista vazia nao apaga a contagem guardada`() =
        runTest {
            repository.refreshWorldCounts()
            filters.next = Result.Success(kidsFilters())

            repository.refreshWorldCounts()

            assertThat(repository.observeWorldCounts().first()).isEqualTo(mapOf(Biome.CERRADO to 11))
        }

    @Test
    fun `uma lista so com biomas desconhecidos tambem nao apaga`() =
        runTest {
            repository.refreshWorldCounts()
            filters.next = Result.Success(kidsFilters(BiomeFilter("marte", "Marte", 3)))

            repository.refreshWorldCounts()

            assertThat(repository.observeWorldCounts().first()).isEqualTo(mapOf(Biome.CERRADO to 11))
        }

    @Test
    fun `observeWorldCounts emite de novo depois do refresh`() =
        runTest {
            repository.observeWorldCounts().test {
                assertThat(awaitItem()).isEmpty()
                repository.refreshWorldCounts()
                assertThat(awaitItem()).isEqualTo(mapOf(Biome.CERRADO to 11))
            }
        }

    @Test
    fun `o modulo do Koin entrega o repositorio da contagem`() {
        val koin =
            koinApplication {
                modules(databaseModule, module { single<FiltersRepository> { FakeFiltersRepository() } })
            }.koin

        assertThat(koin.get<WorldCountsRepository>()::class).isEqualTo(LocalWorldCountsRepository::class)
    }
}
