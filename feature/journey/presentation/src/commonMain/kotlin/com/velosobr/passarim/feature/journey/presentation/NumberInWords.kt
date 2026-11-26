// O número por extenso ("cento e quarenta e sete") da conta da trava para
// adultos. A função é pura: recebe o número e as palavras e devolve o texto,
// sem ler nada de fora. As palavras vêm do strings.xml (lidas pela tela da
// trava), para o texto do app ficar todo nos recursos; os testes passam a
// mesma lista escrita à mão.

package com.velosobr.passarim.feature.journey.presentation

/**
 * As palavras que montam os números de 1 a 999:
 * - [units]: de "zero" a "dezenove" (20 palavras, o índice é o número);
 * - [tens]: de "vinte" a "noventa" (8 palavras, do 20 ao 90);
 * - [hundreds]: de "cento" a "novecentos" (9 palavras, do 100 ao 900);
 * - [exactHundred]: o 100 sozinho ("cem", e não "cento");
 * - [and]: o "e" que liga as partes ("vinte e um").
 */
class NumberWords(
    val units: List<String>,
    val tens: List<String>,
    val hundreds: List<String>,
    val exactHundred: String,
    val and: String,
) {
    init {
        // require falha com IllegalArgumentException: uma lista do tamanho errado no strings.xml
        // apareceria aqui, e não como uma palavra trocada na pergunta.
        require(units.size == 20 && tens.size == 8 && hundreds.size == 9) { "listas de palavras incompletas" }
    }
}

/**
 * Escreve [number] (de 1 a 999) por extenso, no jeito do português: as partes (centena, dezena e
 * unidade) são ligadas por "e", e o 100 sozinho é "cem". Exemplos: 13 → "treze", 21 → "vinte e um",
 * 101 → "cento e um", 891 → "oitocentos e noventa e um".
 */
fun numberInWords(
    number: Int,
    words: NumberWords,
): String {
    require(number in 1..999) { "só de 1 a 999: $number" }
    if (number == 100) return words.exactHundred
    val hundred = number / 100
    val rest = number % 100
    // buildList junta só as partes que existem: 200 não tem dezena nem unidade, 13 não tem centena.
    val parts =
        buildList {
            if (hundred > 0) add(words.hundreds[hundred - 1])
            when {
                rest == 0 -> Unit
                rest < 20 -> add(words.units[rest])
                else -> {
                    add(words.tens[rest / 10 - 2])
                    if (rest % 10 > 0) add(words.units[rest % 10])
                }
            }
        }
    return parts.joinToString(" ${words.and} ")
}
