// A "Ave do dia" do Explorar adulto (frente B traz o dado, a frente E a
// tela): card largo com a foto, um selo no alto, o nome e o nome científico
// embaixo e o botão de canto em forma de cookie no canto.

package com.velosobr.piuds.components.adult

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.velosobr.passarim.core.domain.Biome
import com.velosobr.piuds.components.PiuCookieShape
import com.velosobr.piuds.resources.Res
import com.velosobr.piuds.resources.ic_pause
import com.velosobr.piuds.resources.ic_play
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.toBrush
import org.jetbrains.compose.resources.vectorResource

/**
 * O card da "Ave do dia". [badge] é o texto do selo ("AVE DO DIA ·
 * PANTANAL"), já pronto. Sem canto ([onPlay] nulo), o botão some: a ave do
 * dia pode não ter canto numa linha antiga do catalog. O card cresce para
 * caber nomes longos e fonte grande (no mínimo 196 dp, como na prancheta).
 */
@Composable
fun PiuFeaturedCard(
    badge: String,
    name: String,
    scientificName: String,
    imageUrl: String?,
    biome: Biome,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    playing: Boolean = false,
    onPlay: (() -> Unit)? = null,
    playDescription: String = "",
) {
    Box(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 196.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(PiuTheme.tokens.gradients.getValue(biome).toBrush())
            .clickable(onClick = onClick),
    ) {
        if (imageUrl != null) {
            AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
        PhotoScrim(Modifier.matchParentSize())
        Text(
            badge,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 18.dp, top = 16.dp)
                    .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
        )
        Column(Modifier.align(Alignment.BottomStart).padding(start = 18.dp, end = if (onPlay != null) 84.dp else 18.dp, bottom = 16.dp, top = 56.dp)) {
            Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(scientificName, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = Color.White.copy(alpha = 0.9f))
        }
        if (onPlay != null) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(56.dp)
                    .clip(PiuCookieShape())
                    .background(Color.White)
                    .clickable(role = Role.Button, onClick = onPlay)
                    .semantics { contentDescription = playDescription },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    vectorResource(if (playing) Res.drawable.ic_pause else Res.drawable.ic_play),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}
