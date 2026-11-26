// Testes do número por extenso da trava para adultos, de 3 (o menor fator)
// a 891 (99 × 9, a maior resposta). As palavras vêm de PtBrNumberWords, a
// mesma lista do strings.xml, escrita aqui para o teste não depender dos
// recursos do Compose.

package com.velosobr.passarim.feature.journey.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NumberInWordsTest {
    private fun words(number: Int) = numberInWords(number, PtBrNumberWords)

    @Test
    fun `unidades e os numeros de dez a dezenove`() {
        assertThat(words(3)).isEqualTo("três")
        assertThat(words(9)).isEqualTo("nove")
        assertThat(words(11)).isEqualTo("onze")
        assertThat(words(13)).isEqualTo("treze")
        assertThat(words(19)).isEqualTo("dezenove")
    }

    @Test
    fun `dezenas exatas e com unidade`() {
        assertThat(words(20)).isEqualTo("vinte")
        assertThat(words(21)).isEqualTo("vinte e um")
        assertThat(words(57)).isEqualTo("cinquenta e sete")
        assertThat(words(99)).isEqualTo("noventa e nove")
    }

    @Test
    fun `cem sozinho e cento com o resto`() {
        assertThat(words(100)).isEqualTo("cem")
        assertThat(words(101)).isEqualTo("cento e um")
        assertThat(words(111)).isEqualTo("cento e onze")
        assertThat(words(147)).isEqualTo("cento e quarenta e sete")
    }

    @Test
    fun `centenas exatas e com o resto`() {
        assertThat(words(200)).isEqualTo("duzentos")
        assertThat(words(310)).isEqualTo("trezentos e dez")
        assertThat(words(504)).isEqualTo("quinhentos e quatro")
        assertThat(words(891)).isEqualTo("oitocentos e noventa e um")
    }

    @Test
    fun `fora de 1 a 999 e erro de programacao`() {
        assertFailsWith<IllegalArgumentException> { words(0) }
        assertFailsWith<IllegalArgumentException> { words(1000) }
    }
}
