// O contrato para saber se o BFF tem o Passarim Kids. Fica no core:domain
// porque quem pergunta (a Escolha e as Configurações, na frente D) não deve
// depender do módulo que responde (o feature:explore:data, que já fala com o
// /v1/filters). Assim, a apresentação só conhece esta interface, e os testes
// trocam a resposta por uma falsa.

package com.velosobr.passarim.core.domain

/**
 * Pergunta ao BFF se o Kids está disponível. A resposta não é gravada: cada
 * tela que precisa dela chama de novo ao abrir, porque o catálogo pode ganhar
 * (ou perder) as aves do Kids entre uma abertura e outra.
 */
interface KidsAvailabilityRepository {
    /**
     * true quando o BFF tem aves para o Kids; false quando o BFF não conhece o
     * Kids (anterior à frente B) ou não tem nenhuma ave para ele. Falha só
     * quando não deu para perguntar (sem rede, BFF fora do ar etc.), e aí a
     * tela decide o que mostrar, como o "Tentar de novo".
     */
    suspend fun checkKidsAvailability(): Result<Boolean, DataError.Network>
}
