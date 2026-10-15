package com.velosobr.passarim.feature.settings.data

import coil3.ImageLoader
import com.velosobr.passarim.core.domain.ImageCacheCleaner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CoilImageCacheCleaner(
    private val imageLoader: ImageLoader,
) : ImageCacheCleaner {
    override suspend fun clearImageCache() {
        imageLoader.memoryCache?.clear()
        withContext(Dispatchers.IO) { imageLoader.diskCache?.clear() }
    }
}
