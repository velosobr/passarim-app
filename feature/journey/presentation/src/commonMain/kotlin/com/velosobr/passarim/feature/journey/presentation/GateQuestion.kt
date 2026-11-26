// A conta da trava para adultos ("Quanto é treze vezes sete?") e o sorteio
// dela. O sorteio recebe o Random de fora (injetado): no app é o aleatório
// de verdade, e nos testes um Random com semente fixa, que sorteia sempre a
// mesma sequência e deixa o resultado conferível.

package com.velosobr.passarim.feature.journey.presentation

import kotlin.random.Random

/** Uma multiplicação: [first] vezes [second]. [answer] é o resultado que a pessoa tem de digitar. */
data class GateQuestion(
    val first: Int,
    val second: Int,
) {
    val answer: Int get() = first * second
}

/**
 * Todas as contas possíveis, calculadas uma vez. O primeiro fator vai de 13 a 99, sem múltiplos de
 * 10 nem de 11 (×10 e 22, 33... são fáceis demais de fazer de cabeça); o segundo vai de 3 a 9 (o ×2
 * é só dobrar). São 71 × 7 = 497 contas.
 */
private val AllGateQuestions: List<GateQuestion> =
    (13..99)
        .filter { it % 10 != 0 && it % 11 != 0 }
        .flatMap { first -> (3..9).map { second -> GateQuestion(first, second) } }

/**
 * Sorteia uma conta diferente de [previous]. Em vez de sortear e repetir até sair outra (o que nunca
 * terminaria com um Random que devolve sempre o mesmo número), sorteia uma posição na lista das
 * contas sem a anterior: uma única chamada ao Random, e a repetição é impossível.
 */
fun drawGateQuestion(
    random: Random,
    previous: GateQuestion? = null,
): GateQuestion {
    val candidates = if (previous == null) AllGateQuestions else AllGateQuestions - previous
    return candidates[random.nextInt(candidates.size)]
}
