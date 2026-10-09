package com.velosobr.passarim.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velosobr.passarim.app.debug.DebugRoot
import com.velosobr.passarim.app.navigation.DebugRoute
import com.velosobr.passarim.app.navigation.TabsRoute
import com.velosobr.passarim.app.tabs.TabsScreen
import com.velosobr.passarim.core.designsystem.theme.PassarimTheme
import com.velosobr.passarim.core.domain.AppConfig

/**
 * Raiz do app. O `NavHost` raiz tem as abas (`TabsRoute`) e, só em debug, a tela de prova da conexão.
 * Telas de detalhe e de configurações empilham aqui, por cima das abas, a partir das próximas fatias.
 */
@Composable
fun App(config: AppConfig) {
    PassarimTheme {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val rootNav = rememberNavController()
            NavHost(rootNav, startDestination = TabsRoute) {
                composable<TabsRoute> {
                    TabsScreen(showDebug = config.isDebug, onOpenDebug = { rootNav.navigate(DebugRoute) })
                }
                if (config.isDebug) {
                    composable<DebugRoute> { DebugRoot() }
                }
            }
        }
    }
}
