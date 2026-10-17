// Ponte entre o app Swift e o app Compose no iOS.

package com.velosobr.passarim.app

import androidx.compose.ui.window.ComposeUIViewController
import com.velosobr.passarim.app.di.initKoin
import com.velosobr.passarim.core.domain.AppConfig
import platform.UIKit.UIViewController

/** O Koin só pode ser iniciado uma vez por processo; esta marca evita a segunda vez. */
private var koinStarted = false

/**
 * Ponto de entrada do iOS: o Swift passa a configuração lida do xcconfig/Info.plist.
 * Devolve um UIViewController (a "tela" do UIKit) com o app Compose dentro. O
 * nome em maiúscula segue a convenção do modelo de projeto Compose Multiplatform
 * (a função faz o papel de "fábrica" do controlador); no Swift ela aparece como
 * MainViewControllerKt.MainViewController. Daí o @Suppress das regras de nome.
 */
@Suppress("ktlint:standard:function-naming", "FunctionNaming")
fun MainViewController(
    baseUrl: String,
    isDebug: Boolean,
    mediaHostOverride: String?,
    versionName: String,
): UIViewController {
    val config = AppConfig(isDebug = isDebug, baseUrl = baseUrl, mediaHostOverride = mediaHostOverride, versionName = versionName)
    if (!koinStarted) {
        initKoin(config)
        koinStarted = true
    }
    return ComposeUIViewController { App() }
}
