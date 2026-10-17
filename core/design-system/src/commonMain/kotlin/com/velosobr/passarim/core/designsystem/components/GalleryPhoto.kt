// Dado de uma foto da galeria.

package com.velosobr.passarim.core.designsystem.components

/** Uma foto da galeria: a URL (nula se ainda não há) e a legenda de crédito. */
data class GalleryPhoto(
    val url: String?,
    val caption: String,
)
