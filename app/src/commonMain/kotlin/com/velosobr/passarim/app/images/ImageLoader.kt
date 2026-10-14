package com.velosobr.passarim.app.images

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import io.ktor.client.HttpClient

/** O `ImageLoader` do Coil, baixando as fotos pelo `HttpClient` de mídia (não pelo da API). */
fun passarimImageLoader(
    context: PlatformContext,
    mediaClient: HttpClient,
): ImageLoader =
    ImageLoader
        .Builder(context)
        .components { add(KtorNetworkFetcherFactory(httpClient = mediaClient)) }
        .crossfade(true)
        .build()
