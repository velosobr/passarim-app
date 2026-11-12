// Teste das rotas: os nomes dos argumentos do Detalhe (o id e o autoPlay) são
// as chaves que o ViewModel lê do SavedStateHandle. O Navigation Compose põe
// cada campo da rota no SavedStateHandle com o nome do campo; se um dos dois
// mudar sozinho, o argumento chega vazio e nada avisa.

package com.velosobr.passarim.app.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.feature.detail.presentation.KEY_AUTO_PLAY
import com.velosobr.passarim.feature.detail.presentation.KEY_SPECIES_ID
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
}
