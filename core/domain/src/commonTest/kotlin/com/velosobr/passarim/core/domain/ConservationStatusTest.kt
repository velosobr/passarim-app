// Testes da conversão do código de conservação. Em Kotlin, o nome de um
// teste pode ser uma frase entre crases, o que deixa o relatório legível.

package com.velosobr.passarim.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class ConservationStatusTest {
    @Test
    fun `cada codigo do contrato vira o status correspondente`() {
        listOf("LC", "NT", "VU", "EN", "CR", "EW", "EX", "DD").forEach { code ->
            assertThat(conservationStatusOf(code)?.name).isEqualTo(code)
        }
    }

    @Test
    fun `codigo nulo ou desconhecido vira ausente`() {
        assertThat(conservationStatusOf(null)).isNull()
        assertThat(conservationStatusOf("")).isNull()
        assertThat(conservationStatusOf("XX")).isNull()
        assertThat(conservationStatusOf("lc")).isNull()
    }
}
