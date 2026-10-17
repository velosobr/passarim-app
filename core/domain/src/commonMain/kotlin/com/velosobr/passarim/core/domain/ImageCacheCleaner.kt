// Contrato para limpar o cache de imagens (tela Dados e armazenamento).

package com.velosobr.passarim.core.domain

/** Apaga as imagens guardadas no disco e na memória pelo carregador de imagens. */
interface ImageCacheCleaner {
    suspend fun clearImageCache()
}
