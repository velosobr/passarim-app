// Testes do total de aves que o Explorar mostra no título: a soma das
// contagens por bioma principal (cada ave tem um só bioma principal).

package com.velosobr.passarim.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class FiltersTest {
    private fun filters(primary: List<BiomeFilter>) = Filters(biomes = emptyList(), states = emptyList(), primaryBiomes = primary)

    @Test
    fun `o total e a soma das contagens por bioma principal`() {
        val primary = listOf(BiomeFilter("cerrado", "Cerrado", 7), BiomeFilter("pampa", "Pampa", 4))
        assertThat(filters(primary).speciesTotal).isEqualTo(11)
    }

    @Test
    fun `sem bioma principal o total fica nulo`() {
        // Um BFF de antes da frente B não manda primaryBiomes: o título aparece sem a contagem.
        assertThat(filters(emptyList()).speciesTotal).isNull()
    }

    @Test
    fun `com todas as contagens zeradas o total fica nulo`() {
        assertThat(filters(listOf(BiomeFilter("cerrado", "Cerrado", 0))).speciesTotal).isNull()
    }
}
