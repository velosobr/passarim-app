// A casca do Passarim Kids (frente D): a barra do Kids com as abas Explorar
// e Álbum e o botão "Para adultos". Por enquanto, cada aba mostra só o Pipo
// dizendo "Logo, logo tem aves aqui!"; a frente F troca o conteúdo das abas
// pelas telas Kids de verdade. Fica no app, ao lado do TabsScreen do adulto,
// porque é ele quem conhece todas as features.

package com.velosobr.passarim.app.kids

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.velosobr.passarim.app.resources.Res
import com.velosobr.passarim.app.resources.kids_coming_soon
import com.velosobr.passarim.app.resources.kids_for_adults
import com.velosobr.passarim.app.resources.kids_tab_album
import com.velosobr.passarim.app.resources.kids_tab_explore
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.components.kids.PiuKidsNavAction
import com.velosobr.piuds.components.kids.PiuKidsNavBar
import com.velosobr.piuds.components.kids.PiuPipoBalloon
import com.velosobr.piuds.resources.ic_explore
import com.velosobr.piuds.resources.ic_favorites
import com.velosobr.piuds.resources.ic_lock
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.velosobr.piuds.resources.Res as DesignRes

/** A altura da barra do Kids (76 dp) mais a margem dela até o fim da tela (20 dp). */
private val KidsBarSpace = 96.dp

/** As abas do Kids, na ordem das pranchetas: Explorar · Álbum. */
private enum class KidsTab(
    val label: StringResource,
    val icon: DrawableResource,
) {
    Explore(Res.string.kids_tab_explore, DesignRes.drawable.ic_explore),
    Album(Res.string.kids_tab_album, DesignRes.drawable.ic_favorites),
}

/**
 * A casca. A aba escolhida fica num rememberSaveable: um estado que sobrevive à rotação e à volta da
 * trava (o "Saveable" guarda o valor junto com o estado da tela). Não há um NavHost por aba, como no
 * adulto, porque as abas ainda não têm telas por cima; a frente F decide isso com as telas reais.
 * [onOpenAdults] abre a trava para adultos.
 */
@Composable
fun KidsTabsScreen(onOpenAdults: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf(KidsTab.Explore) }
    val systemBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(Modifier.fillMaxSize()) {
        ComingSoonTab(
            title = stringResource(selected.label),
            modifier = Modifier.padding(bottom = KidsBarSpace + systemBottom),
        )
        val tabs = KidsTab.entries
        PiuKidsNavBar(
            items = tabs.map { PiuNavItem(vectorResource(it.icon), stringResource(it.label)) },
            selected = tabs.indexOf(selected),
            onSelect = { selected = tabs[it] },
            action = PiuKidsNavAction(vectorResource(DesignRes.drawable.ic_lock), stringResource(Res.string.kids_for_adults), onOpenAdults),
            modifier = Modifier.align(Alignment.BottomCenter).padding(start = 16.dp, end = 16.dp, bottom = 20.dp + systemBottom),
        )
    }
}

/** Uma aba da casca: o título (cabeçalho para o leitor de tela) e o Pipo avisando que logo tem aves. */
@Composable
private fun ComingSoonTab(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(start = 16.dp, top = 24.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        PiuPipoBalloon(stringResource(Res.string.kids_coming_soon))
    }
}
