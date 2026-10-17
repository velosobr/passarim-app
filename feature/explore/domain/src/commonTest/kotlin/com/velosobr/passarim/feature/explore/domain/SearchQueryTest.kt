// Testes da limpeza do texto de busca.

package com.velosobr.passarim.feature.explore.domain

import assertk.assertThat
import assertk.assertions.hasLength
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SearchQueryTest {
    @Test
    fun `apara os espacos das pontas`() {
        assertThat(sanitizeQuery("  bem-te-vi  ")).isEqualTo("bem-te-vi")
    }

    @Test
    fun `caracteres de controle viram espaco`() {
        assertThat(sanitizeQuery("arara\nazul\t")).isEqualTo("arara azul")
    }

    @Test
    fun `limita a 100 caracteres`() {
        assertThat(sanitizeQuery("a".repeat(150))).hasLength(MAX_QUERY_LENGTH)
    }

    @Test
    fun `so espacos vira texto vazio`() {
        assertThat(sanitizeQuery(" \n ")).isEqualTo("")
    }

    @Test
    fun `acentos e hifens passam intactos`() {
        assertThat(sanitizeQuery("Sabiá-laranjeira")).isEqualTo("Sabiá-laranjeira")
    }
}
