package com.velosobr.passarim.feature.detail.domain

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class SpeciesIdTest {
    @Test
    fun `aceita os ids do catalogo`() {
        assertThat(isValidSpeciesId("anodorhynchus-hyacinthinus")).isTrue()
        assertThat(isValidSpeciesId("a")).isTrue()
        assertThat(isValidSpeciesId("a".repeat(80))).isTrue()
    }

    @Test
    fun `recusa vazio maiusculas barras espacos e mais de 80 caracteres`() {
        listOf("", "Arara", "../admin", "ara ra", "ara/azul", "ara%2F", "a".repeat(81), "ará").forEach {
            assertThat(isValidSpeciesId(it)).isFalse()
        }
    }
}
