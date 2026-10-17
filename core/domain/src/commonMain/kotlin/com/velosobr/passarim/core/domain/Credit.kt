// Crédito de autoria de uma foto, de um canto ou de um texto.

package com.velosobr.passarim.core.domain

/**
 * Quem fez, sob qual licença e de onde veio. As licenças Creative Commons
 * exigem mostrar o autor e a licença. Tudo pode faltar (null) quando o BFF
 * não manda.
 */
data class Credit(
    val author: String?,
    val license: String?,
    val source: String?,
    val sourceUrl: String?,
)
