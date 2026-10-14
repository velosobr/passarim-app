package com.velosobr.passarim.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.setSingletonImageLoaderFactory
import com.velosobr.passarim.app.images.passarimImageLoader
import com.velosobr.passarim.app.navigation.DetailRoute
import com.velosobr.passarim.app.navigation.TabsRoute
import com.velosobr.passarim.app.tabs.TabsScreen
import com.velosobr.passarim.core.data.MediaHttpClientQualifier
import com.velosobr.passarim.core.designsystem.theme.PassarimTheme
import com.velosobr.passarim.feature.detail.presentation.DetailRoot
import io.ktor.client.HttpClient
import org.koin.compose.koinInject

/**
 * Raiz do app. O `NavHost` raiz tem as abas (`TabsRoute`) e, por cima delas, as telas cheias, como o
 * detalhe (`DetailRoute`). O Coil é configurado aqui uma vez, com o `HttpClient` de mídia.
 */
@Composable
fun App() {
    val mediaClient = koinInject<HttpClient>(MediaHttpClientQualifier)
    setSingletonImageLoaderFactory { context -> passarimImageLoader(context, mediaClient) }
    PassarimTheme {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
    }
}
