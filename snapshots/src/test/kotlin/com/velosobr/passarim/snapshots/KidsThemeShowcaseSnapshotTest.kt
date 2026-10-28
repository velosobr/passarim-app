// Mostruário do tema Kids: as cores principais, os gradientes de mundo e os
// estilos de texto. Não é uma tela do app; é a prova visual do tema antes de
// existirem os componentes Kids (plano C2).

package com.velosobr.passarim.snapshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.theme.PiuTheme
import org.junit.Test

class KidsThemeShowcaseSnapshotTest(
    fontScalePercent: Int,
) : KidsSnapshotTest(fontScalePercent) {
    @Test
    fun showcase() =
        snap("kids-theme") {
            val colors = MaterialTheme.colorScheme
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Oi! Eu sou o Pipo.", style = MaterialTheme.typography.headlineLarge)
                Text("Qual ave vamos conhecer hoje?", style = MaterialTheme.typography.titleMedium)
                Text("O sabiá tem a barriga cor de laranja.", style = MaterialTheme.typography.bodyLarge)
                Text("Ouvir o canto!", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(colors.primaryContainer, colors.secondary, colors.tertiary, colors.inverseSurface, colors.inversePrimary)
                        .forEach { Box(Modifier.size(40.dp).background(it, MaterialTheme.shapes.small)) }
                }
                Biome.entries.forEach { biome ->
                    val g = PiuTheme.tokens.gradients.getValue(biome)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(g.from, g.to)), MaterialTheme.shapes.medium)
                            .padding(12.dp),
                    ) { Text(biome.name, color = g.content, style = MaterialTheme.typography.titleMedium) }
                }
            }
        }
}
