// A tela das abas do adulto: o NavHost de cada aba e, flutuando por cima, a
// barra de navegação em pílula do PiuDS, que desfoca o conteúdo que passa por
// baixo dela (frente E). "by" lê o State como valor comum;
// currentBackStackEntryAsState recompõe a barra quando a aba muda.

package com.velosobr.passarim.app.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.velosobr.passarim.app.navigation.ExploreRoute
import com.velosobr.passarim.app.navigation.FavoritesRoute
import com.velosobr.passarim.app.navigation.SettingsRoute
import com.velosobr.passarim.feature.explore.presentation.ExploreRoot
import com.velosobr.passarim.feature.favorites.presentation.FavoritesRoot
import com.velosobr.passarim.feature.settings.presentation.SettingsRoot
import com.velosobr.piuds.components.PiuNavItem
import com.velosobr.piuds.components.adult.PiuFloatingNavBar
import com.velosobr.piuds.components.piuBlurSource
import com.velosobr.piuds.components.rememberPiuBlurState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/** A altura da barra flutuante (68 dp) mais a margem dela até o fim da tela (24 dp). */
private val FloatingBarSpace = 92.dp

/**
 * As abas com um `NavHost` interno: cada aba tem a sua pilha, e trocar de aba guarda e restaura o
 * estado (`saveState`/`restoreState`). O conteúdo ocupa a tela inteira e é a fonte do desfoque; a
 * barra flutua por cima, acima da área do gesto de navegação do sistema (`navigationBars`). Cada
 * tela recebe o espaço da barra para o fim da lista não ficar escondido atrás dela.
 */
@Composable
fun TabsScreen(
    onOpenDetail: (id: String, autoPlay: Boolean) -> Unit,
    onOpenStorage: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val blur = rememberPiuBlurState()
    val systemBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val barPadding = PaddingValues(bottom = FloatingBarSpace + systemBottom)
    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController,
            startDestination = ExploreRoute,
            modifier = Modifier.fillMaxSize().piuBlurSource(blur).statusBarsPadding(),
        ) {
            composable<ExploreRoute> {
                ExploreRoot(
                    onOpenDetail = onOpenDetail,
                    onOpenFavorites = { navController.navigateToTab(Tab.Favorites) },
                    contentPadding = barPadding,
                )
            }
            composable<FavoritesRoute> {
                FavoritesRoot(
                    onOpenDetail = { id -> onOpenDetail(id, false) },
                    onOpenExplore = { navController.navigateToTab(Tab.Explore) },
                    contentPadding = barPadding,
                )
            }
            composable<SettingsRoute> {
                SettingsRoot(onOpenStorage = onOpenStorage, onOpenAbout = onOpenAbout, contentPadding = barPadding)
            }
        }
        val tabs = Tab.entries
        PiuFloatingNavBar(
            items = tabs.map { PiuNavItem(vectorResource(it.icon), stringResource(it.label)) },
            selected = tabs.indexOfFirst { backStack?.destination?.hasRoute(it.route::class) == true }.coerceAtLeast(0),
            onSelect = { navController.navigateToTab(tabs[it]) },
            blurState = blur,
            modifier = Modifier.align(Alignment.BottomCenter).padding(start = 24.dp, end = 24.dp, bottom = 24.dp + systemBottom),
        )
    }
}

/** Troca de aba como a barra inferior faz: uma instância por aba, guardando o estado da que sai. */
private fun NavHostController.navigateToTab(tab: Tab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
