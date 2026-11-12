// Teste das rotas: os nomes dos argumentos do Detalhe (o id, o autoPlay e a
// prévia do topo) são as chaves que o ViewModel lê do SavedStateHandle. O
// Navigation Compose põe cada campo da rota no SavedStateHandle com o nome do
// campo; se um dos dois mudar sozinho, o argumento chega vazio e nada avisa.

package com.velosobr.passarim.app.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.passarim.core.presentation.DetailOpening
import com.velosobr.passarim.core.presentation.DetailPreview
import com.velosobr.passarim.feature.detail.presentation.KEY_AUTO_PLAY
import com.velosobr.passarim.feature.detail.presentation.KEY_PHOTO_KEY
import com.velosobr.passarim.feature.detail.presentation.KEY_PRIMARY_BIOME
import com.velosobr.passarim.feature.detail.presentation.KEY_SPECIES_ID
import com.velosobr.passarim.feature.detail.presentation.KEY_THUMBNAIL_URL
import kotlin.test.Test

class RoutesTest {
    @Test
    fun `o argumento da rota do Detalhe e a chave que o ViewModel le`() {
        assertThat(DetailRoute::id.name).isEqualTo(KEY_SPECIES_ID)
    }

    @Test
    fun `o autoPlay da rota do Detalhe e a chave que o ViewModel le`() {
        assertThat(DetailRoute::autoPlay.name).isEqualTo(KEY_AUTO_PLAY)
    }

    @Test
    fun `os campos da previa da rota do Detalhe sao as chaves que o ViewModel le`() {
        assertThat(DetailRoute::photoKey.name).isEqualTo(KEY_PHOTO_KEY)
        assertThat(DetailRoute::thumbnailUrl.name).isEqualTo(KEY_THUMBNAIL_URL)
        assertThat(DetailRoute::primaryBiome.name).isEqualTo(KEY_PRIMARY_BIOME)
    }

    @Test
    fun `o pedido de abrir o Detalhe vira a rota com o bioma pelo codigo do BFF`() {
        val preview = DetailPreview("foto-tuiuiu", "https://thumb/t.jpg", Biome.PANTANAL)
        val opening = DetailOpening("tuiuiu", autoPlay = true, preview = preview)
        assertThat(opening.toRoute()).isEqualTo(DetailRoute("tuiuiu", true, "foto-tuiuiu", "https://thumb/t.jpg", "pantanal"))
    }
}
