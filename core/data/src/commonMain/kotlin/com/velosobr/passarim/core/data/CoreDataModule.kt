// Registro das peças de rede no Koin.

package com.velosobr.passarim.core.data

import org.koin.dsl.module

/**
 * Módulo Koin da camada de rede. O `AppConfig` vem do módulo do `app`.
 *
 * Koin é a biblioteca de injeção de dependências: aqui declaramos COMO criar
 * cada peça, e quem precisa dela a recebe pronta. "single" cria uma instância
 * só para o app inteiro, e "get()" pede ao Koin uma peça já registrada. O
 * qualificador separa os dois HttpClient (API e mídia), que têm o mesmo tipo.
 */
val coreDataModule =
    module {
        single { createHttpClient(platformHttpEngine(), get()) }
        single(MediaHttpClientQualifier) { createMediaHttpClient(platformHttpEngine()) }
        single { MediaUrlRewriter(get()) }
    }
