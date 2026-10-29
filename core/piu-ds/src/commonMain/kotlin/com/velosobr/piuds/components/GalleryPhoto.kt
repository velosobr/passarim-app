// Dado de uma foto da galeria.

package com.velosobr.piuds.components

/** Uma foto da galeria: a URL (nula se ainda não há) e a legenda de crédito. */
data class GalleryPhoto(
    val url: String?,
    val caption: String,
)
