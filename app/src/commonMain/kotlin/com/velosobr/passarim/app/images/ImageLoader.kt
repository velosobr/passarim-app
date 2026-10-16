// O carregador de imagens (Coil) do app.

package com.velosobr.passarim.app.images

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.velosobr.passarim.core.data.MediaHttpClientQualifier
import io.ktor.client.HttpClient
import org.koin.core.scope.Scope
import org.koin.dsl.module

/**
 * Cria o ImageLoader do Coil usando o cliente HTTP de mídia (Ktor), com uma
 * transição suave (crossfade) quando a foto chega.
 */
fun passarimImageLoader(
    context: PlatformContext,
    mediaClient: HttpClient,
): ImageLoader =
    ImageLoader
        .Builder(context)
        .components { add(KtorNetworkFetcherFactory(httpClient = mediaClient)) }
        .crossfade(true)
        .build()

/** O contexto que o Coil exige em cada plataforma. */
internal expect fun Scope.platformContext(): PlatformContext

/** Registro do ImageLoader no Koin, uma instância só para o app. */
internal val imageLoaderModule =
    module {
        single<ImageLoader> { passarimImageLoader(platformContext(), get(MediaHttpClientQualifier)) }
    }
