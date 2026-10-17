// A limpeza do cache de imagens.

package com.velosobr.passarim.feature.settings.data

import coil3.ImageLoader
import com.velosobr.passarim.core.domain.ImageCacheCleaner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Limpa o cache de imagens do Coil: o da memória na hora e o do disco no
 * Dispatchers.IO, porque apagar arquivos é trabalho de disco.
 */
class CoilImageCacheCleaner(
    private val imageLoader: ImageLoader,
) : ImageCacheCleaner {
    override suspend fun clearImageCache() {
        imageLoader.memoryCache?.clear()
        withContext(Dispatchers.IO) { imageLoader.diskCache?.clear() }
    }
}
