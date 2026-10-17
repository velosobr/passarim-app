// Conferência do id de espécie que chega pela navegação.

package com.velosobr.passarim.feature.detail.domain

/** O formato do id: minúsculas, números e hífen, até 80 caracteres (o mesmo que o BFF aceita). */
private val SPECIES_ID = Regex("^[a-z0-9-]{1,80}$")

/** Um id fora do formato nem vai ao BFF: a tela mostra "não encontrada". */
fun isValidSpeciesId(id: String): Boolean = SPECIES_ID.matches(id)
