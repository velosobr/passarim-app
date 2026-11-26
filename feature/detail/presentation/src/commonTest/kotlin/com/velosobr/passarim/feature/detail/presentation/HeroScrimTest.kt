// Testes do degradê na base da foto do Detalhe (o scrim atrás do nome e a
// faixa que funde a foto ao fundo da tela): as paradas de cor que o topo
// desenha, no tema escuro e no claro.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.ui.graphics.Color
import assertk.assertThat
import assertk.assertions.containsExactly
import kotlin.test.Test

class HeroScrimTest {
    /** O scrim dos tokens (#101410 com 80%) e os fundos do Piu escuro e claro. */
    private val scrim = Color(0xCC101410)
    private val darkBackground = Color(0xFF101410)
    private val lightBackground = Color(0xFFF6FBF2)

    /**
     * No escuro, como na prancheta, o degradê não para no scrim: depois de chegar a ele, segue
     * escurecendo até a cor do fundo, sem a faixa cinza de cor parada sobre uma foto clara.
     */
    @Test
    fun `no escuro o degrade segue do scrim ate a cor do fundo sem faixa parada`() {
        val stops = heroScrimStops(scrim, darkBackground, dark = true, fadeEnd = 0.2f, textEnd = 0.8f)
        assertThat(stops).containsExactly(
            0f to scrim.copy(alpha = 0f),
            0.2f to scrim,
            1f to darkBackground,
        )
    }

    /**
     * No claro, o texto branco fica todo sobre o scrim cheio (o contraste), e só depois dele a faixa
     * vai do scrim ao fundo claro.
     */
    @Test
    fun `no claro o scrim fica cheio atras do texto e so depois vai ao fundo`() {
        val stops = heroScrimStops(scrim, lightBackground, dark = false, fadeEnd = 0.2f, textEnd = 0.8f)
        assertThat(stops).containsExactly(
            0f to scrim.copy(alpha = 0f),
            0.2f to scrim,
            0.8f to scrim,
            1f to lightBackground,
        )
    }
}
