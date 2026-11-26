// O que a tela mostra do Passarim Kids depois de perguntar ao BFF se ele
// existe (KidsAvailabilityRepository, no core:domain). Fica no
// core:presentation porque duas features diferentes usam os mesmos estados:
// a Escolha (feature:journey) e as Configurações (feature:settings), e uma
// feature não deve depender da outra.

package com.velosobr.passarim.core.presentation

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result

/**
 * Os quatro estados do Kids na tela (spec da frente D, 4.1):
 * - CHECKING: a pergunta ao BFF ainda não voltou; o Kids aparece, mas não dá para entrar;
 * - AVAILABLE: o BFF tem aves para o Kids; dá para entrar;
 * - OFFLINE: não deu para perguntar (sem rede ou erro); o Kids aparece apagado, com "Tentar de novo";
 * - HIDDEN: o BFF não conhece o Kids ou não tem aves para ele; o Kids não aparece.
 *
 * [canEnter] diz se o toque leva ao Kids. Um enum com propriedade no construtor deixa cada valor
 * carregar a sua resposta, sem um "when" espalhado pelas telas.
 */
enum class KidsAvailabilityState(
    val canEnter: Boolean,
) {
    CHECKING(canEnter = false),
    AVAILABLE(canEnter = true),
    OFFLINE(canEnter = false),
    HIDDEN(canEnter = false),
}

/**
 * Traduz a resposta do repositório: true vira AVAILABLE, false vira HIDDEN e qualquer erro de rede
 * vira OFFLINE (a tela não distingue "sem internet" de "BFF fora do ar": nos dois, tentar de novo
 * é o que resta).
 */
fun Result<Boolean, DataError.Network>.toKidsAvailabilityState(): KidsAvailabilityState =
    when (this) {
        is Result.Success -> if (data) KidsAvailabilityState.AVAILABLE else KidsAvailabilityState.HIDDEN
        is Result.Failure -> KidsAvailabilityState.OFFLINE
    }
