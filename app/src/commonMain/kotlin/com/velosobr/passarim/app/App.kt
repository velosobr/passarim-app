package com.velosobr.passarim.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.velosobr.passarim.app.navigation.DetailRoute
import com.velosobr.passarim.app.navigation.TabsRoute
import com.velosobr.passarim.app.tabs.TabsScreen
import com.velosobr.passarim.app.theme.isDark
import com.velosobr.passarim.core.designsystem.theme.PassarimTheme
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.feature.detail.presentation.DetailRoot
import org.koin.compose.koinInject

@Composable
fun App(onSettingsLoaded: () -> Unit = {}) {
    val imageLoader = koinInject<ImageLoader>()
    setSingletonImageLoaderFactory { imageLoader }
    val settingsRepository = koinInject<SettingsRepository>()
    val settings by remember(settingsRepository) { settingsRepository.observe() }.collectAsState(initial = null)
    val loaded = settings ?: return
    LaunchedEffect(Unit) { onSettingsLoaded() }
    PassarimTheme(darkTheme = loaded.theme.isDark(isSystemInDarkTheme()), dynamicColor = loaded.dynamicColors) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AppNavHost()
        }
    }
}

@Composable
private fun AppNavHost() {
    val rootNav = rememberNavController()
    NavHost(rootNav, startDestination = TabsRoute) {
        composable<TabsRoute> {
            TabsScreen(onOpenDetail = { id -> rootNav.navigate(DetailRoute(id)) })
        }
        composable<DetailRoute> {
            DetailRoot(onBack = dropUnlessResumed { rootNav.popBackStack() })
        }
    }
}
