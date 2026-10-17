// Teste das rotas: o nome do argumento do Detalhe é o que o ViewModel lê do
// SavedStateHandle.

package com.velosobr.passarim.app.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.feature.detail.presentation.KEY_SPECIES_ID
import kotlin.test.Test

class RoutesTest {
    @Test
    fun `o argumento da rota do Detalhe e a chave que o ViewModel le`() {
        assertThat(DetailRoute::id.name).isEqualTo(KEY_SPECIES_ID)
    }
}
