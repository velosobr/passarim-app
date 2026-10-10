package com.velosobr.passarim.feature.explore.domain

/** O BFF recusa `q` com mais de 100 caracteres (400 INVALID_PARAMETER). */
const val MAX_QUERY_LENGTH = 100

/**
 * Limpa o texto digitado antes de virar o parâmetro `q`: caracteres de controle (um `\n` colado,
 * por exemplo) viram espaço, as pontas são aparadas e o resultado tem no máximo [MAX_QUERY_LENGTH].
 */
fun sanitizeQuery(raw: String): String =
    raw
        .map { if (it.isISOControl()) ' ' else it }
        .joinToString("")
        .trim()
        .take(MAX_QUERY_LENGTH)
        .trimEnd()
