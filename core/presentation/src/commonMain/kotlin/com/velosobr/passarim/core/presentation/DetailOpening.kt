// O pedido de abrir o Detalhe de uma ave, que as telas de lista (Explorar,
// Favoritos) entregam ao app para ele navegar. Mora aqui, no módulo que todas
// as telas usam, porque quem pede (o Explorar) e quem recebe (o Detalhe) são
// módulos que não se conhecem; o app converte o pedido na rota do Detalhe.

package com.velosobr.passarim.core.presentation

import com.velosobr.passarim.core.domain.Biome

/**
 * O que o card tocado já sabe da ave e o Detalhe pode desenhar antes de a ave chegar da rede: a
 * [photoKey] do elemento compartilhado (a foto que viaja do card, veja SharedPhotoKey.kt), a
 * [thumbnailUrl] da miniatura, que o carregador de imagens já tem guardada, e o [biome] principal,
 * cujo gradiente fica atrás da foto. Tudo é opcional: sem chave, a foto não viaja (abrir pelos
 * Favoritos, por exemplo); sem miniatura nem bioma, o topo mostra o bloco cinza do carregamento.
 */
data class DetailPreview(
    val photoKey: String? = null,
    val thumbnailUrl: String? = null,
    val biome: Biome? = null,
)

/** Abrir o Detalhe da ave [id]; com [autoPlay], o canto começa ao abrir; [preview] é o que o card já mostra. */
data class DetailOpening(
    val id: String,
    val autoPlay: Boolean = false,
    val preview: DetailPreview = DetailPreview(),
)
