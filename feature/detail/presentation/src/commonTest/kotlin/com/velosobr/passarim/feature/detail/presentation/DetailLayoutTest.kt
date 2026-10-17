// Testes do layout dos fatos e da formatação de tempo e crédito.

package com.velosobr.passarim.feature.detail.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.core.domain.Credit
import kotlin.test.Test

class DetailLayoutTest {
    @Test
    fun `fatos em 3 colunas ate fonte 1_3 e em coluna unica a partir de 1_5`() {
        assertThat(factsInSingleColumn(1.0f)).isFalse()
        assertThat(factsInSingleColumn(1.3f)).isFalse()
        assertThat(factsInSingleColumn(1.5f)).isTrue()
        assertThat(factsInSingleColumn(2.0f)).isTrue()
    }

    @Test
    fun `o tempo do player aparece como minutos e segundos`() {
        assertThat(formatTime(0)).isEqualTo("0:00")
        assertThat(formatTime(12_400)).isEqualTo("0:12")
        assertThat(formatTime(31_000)).isEqualTo("0:31")
        assertThat(formatTime(125_000)).isEqualTo("2:05")
        assertThat(formatTime(-5)).isEqualTo("0:00")
    }

    @Test
    fun `a linha de credito usa autor e licenca e os textos de ausencia`() {
        assertThat(creditLine(Credit("Paul Donahue", "CC BY-NC", "inaturalist", null), "sem autor", "sem licença"))
            .isEqualTo("Paul Donahue · CC BY-NC")
        assertThat(creditLine(Credit(null, null, null, null), "Autor não informado", "Licença não informada"))
            .isEqualTo("Autor não informado · Licença não informada")
    }
}
