package com.velosobr.passarim.app.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.velosobr.passarim.app.resources.Res
import com.velosobr.passarim.app.resources.placeholder_in_progress
import com.velosobr.passarim.feature.explore.presentation.ExploreRoot
import com.velosobr.passarim.feature.favorites.presentation.FavoritesRoot
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Scaffold com a barra inferior e um `NavHost` interno: cada aba tem a sua pilha, e trocar de aba
 * guarda e restaura o estado (`saveState`/`restoreState`).
 */
@Composable
fun TabsScreen(onOpenDetail: (String) -> Unit) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = backStack?.destination?.hasRoute(tab.route::class) == true,
                        onClick = { navController.navigateToTab(tab) },
                        icon = { Icon(vectorResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.label)) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(navController, startDestination = ExploreRoute, modifier = Modifier.padding(padding)) {
            composable<ExploreRoute> {
                ExploreRoot(onOpenDetail = onOpenDetail, onOpenFavorites = { navController.navigateToTab(Tab.Favorites) })
            }
            composable<FavoritesRoute> {
                FavoritesRoot(onOpenDetail = onOpenDetail, onOpenExplore = { navController.navigateToTab(Tab.Explore) })
            }
            composable<SettingsRoute> { TabPlaceholder(Tab.Settings) }
        }
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

/** Conteúdo vazio das abas; a fatia 5d troca isto pelas telas reais. */
@Composable
private fun TabPlaceholder(tab: Tab) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(tab.label))
            Text(stringResource(Res.string.placeholder_in_progress))
        }
    }
}
