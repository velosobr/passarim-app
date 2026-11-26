// As palavras dos números em pt-BR, iguais às do strings.xml do módulo, para
// os testes que não carregam os recursos do Compose.

package com.velosobr.passarim.feature.journey.presentation

/** A lista de palavras que o app lê do strings.xml, copiada para os testes. */
val PtBrNumberWords =
    NumberWords(
        units =
            listOf(
                "zero",
                "um",
                "dois",
                "três",
                "quatro",
                "cinco",
                "seis",
                "sete",
                "oito",
                "nove",
                "dez",
                "onze",
                "doze",
                "treze",
                "quatorze",
                "quinze",
                "dezesseis",
                "dezessete",
                "dezoito",
                "dezenove",
            ),
        tens = listOf("vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"),
        hundreds =
            listOf(
                "cento",
                "duzentos",
                "trezentos",
                "quatrocentos",
                "quinhentos",
                "seiscentos",
                "setecentos",
                "oitocentos",
                "novecentos",
            ),
        exactHundred = "cem",
        and = "e",
    )
