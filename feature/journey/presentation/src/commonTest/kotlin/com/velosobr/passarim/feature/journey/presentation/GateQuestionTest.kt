// Testes do sorteio da conta da trava para adultos, com um Random fixo (a
// semente) para o resultado ser o mesmo a cada execução.

package com.velosobr.passarim.feature.journey.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import kotlin.random.Random
import kotlin.test.Test

class GateQuestionTest {
    /** Um Random que sempre sorteia o primeiro da lista: mostra que a conta anterior fica de fora. */
    private val alwaysFirst =
        object : Random() {
            override fun nextBits(bitCount: Int) = 0
        }

    @Test
    fun `o primeiro fator vai de 13 a 99 sem multiplos de 10 e de 11 e o segundo de 3 a 9`() {
        val random = Random(42)
        val firsts = mutableSetOf<Int>()
        repeat(2000) {
            val question = drawGateQuestion(random)
            assertThat(question.first in 13..99).isTrue()
            assertThat(question.first % 10 != 0 && question.first % 11 != 0).isTrue()
            assertThat(question.second).isIn(3, 4, 5, 6, 7, 8, 9)
            firsts += question.first
        }
        // 87 números de 13 a 99, menos 8 múltiplos de 10 e 8 de 11: com 2000 sorteios, todos saem.
        assertThat(firsts.size).isEqualTo(71)
    }

    @Test
    fun `a conta nova nunca repete a anterior`() {
        val random = Random(7)
        var previous = drawGateQuestion(random)
        repeat(2000) {
            val next = drawGateQuestion(random, previous)
            assertThat(next).isNotEqualTo(previous)
            previous = next
        }
    }

    @Test
    fun `mesmo um Random que sorteia sempre igual nao repete a anterior`() {
        val first = drawGateQuestion(alwaysFirst)
        assertThat(first).isEqualTo(GateQuestion(13, 3))
        assertThat(drawGateQuestion(alwaysFirst, previous = first)).isNotEqualTo(first)
    }

    @Test
    fun `a resposta e o produto dos fatores`() {
        assertThat(GateQuestion(13, 7).answer).isEqualTo(91)
        assertThat(GateQuestion(99, 9).answer).isEqualTo(891)
    }
}
