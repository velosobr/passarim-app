package com.velosobr.passarim.app.images

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.velosobr.passarim.core.data.MediaHttpClientQualifier
import io.ktor.client.HttpClient
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun passarimImageLoader(
    context: PlatformContext,
    mediaClient: HttpClient,
): ImageLoader =
    ImageLoader
        .Builder(context)
        .components { add(KtorNetworkFetcherFactory(httpClient = mediaClient)) }
        .crossfade(true)
        .build()

internal expect fun Scope.platformContext(): PlatformContext

internal val imageLoaderModule =
    module {
        single<ImageLoader> { passarimImageLoader(platformContext(), get(MediaHttpClientQualifier)) }
    }
