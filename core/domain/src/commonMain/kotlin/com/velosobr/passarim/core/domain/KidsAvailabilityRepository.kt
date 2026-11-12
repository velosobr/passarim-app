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
     * Kids (anterior à frente B, ou que recusa o pedido do Kids como parâmetro
     * inválido) ou não tem nenhuma ave para ele. Falha quando não deu para
     * perguntar (sem rede, BFF fora do ar etc.) ou quando a resposta veio
     * num formato que o app não entende, e aí a tela decide o que mostrar,
     * como o "Tentar de novo".
     *
     * É "suspend" porque espera a resposta da rede: a função pode pausar sem
     * travar a tela (roda numa coroutine) e só pode ser chamada de dentro de
     * outra função suspend ou de uma coroutine. O Result<Boolean,
     * DataError.Network> obriga quem chama a tratar os dois casos: o
     * Success traz o true/false, e o Failure traz o erro de rede tipado.
     */
    suspend fun checkKidsAvailability(): Result<Boolean, DataError.Network>
}
