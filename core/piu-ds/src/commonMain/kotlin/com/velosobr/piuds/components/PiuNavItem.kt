// Um destino das barras de navegação do PiuDS (a flutuante do adulto e a do
// Kids). Fica entre os componentes comuns porque as duas barras o usam.

package com.velosobr.piuds.components

import androidx.compose.ui.graphics.vector.ImageVector

/** Um destino da barra: o ícone e o rótulo (que também é a descrição para o leitor de tela). */
data class PiuNavItem(
    val icon: ImageVector,
    val label: String,
)
