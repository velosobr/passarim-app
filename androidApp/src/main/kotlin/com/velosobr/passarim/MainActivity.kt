// A Activity do Android: a tela onde o app Compose é desenhado.

package com.velosobr.passarim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.velosobr.passarim.app.App

/**
 * Segura a splash até as configurações chegarem (para a primeira tela já sair
 * no tema certo), desenha atrás das barras do sistema (edge-to-edge) e mostra
 * o App. setKeepOnScreenCondition é consultada a cada quadro enquanto devolve
 * true.
 */
class MainActivity : ComponentActivity() {
    /** Vira true quando o App avisa que as configurações foram lidas. */
    private var settingsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !settingsLoaded }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App(onSettingsLoaded = { settingsLoaded = true }) }
    }
}
