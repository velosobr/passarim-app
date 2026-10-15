package com.velosobr.passarim.core.domain

interface ImageCacheCleaner {
    suspend fun clearImageCache()
}
