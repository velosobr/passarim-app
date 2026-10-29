// A raiz do app: tema, carregador de imagens e a navegação de primeiro nível.

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
import com.velosobr.passarim.app.navigation.AboutRoute
import com.velosobr.passarim.app.navigation.DetailRoute
import com.velosobr.passarim.app.navigation.LicensesRoute
import com.velosobr.passarim.app.navigation.StorageRoute
import com.velosobr.passarim.app.navigation.TabsRoute
import com.velosobr.passarim.app.tabs.TabsScreen
import com.velosobr.passarim.app.theme.SystemBarsAppearance
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.feature.detail.presentation.DetailRoot
import com.velosobr.passarim.feature.settings.presentation.AboutRoot
import com.velosobr.passarim.feature.settings.presentation.LicensesRoot
import com.velosobr.passarim.feature.settings.presentation.StorageRoot
import com.velosobr.piuds.theme.PiuTheme
import com.velosobr.piuds.theme.isDark
import org.koin.compose.koinInject

/**
 * O app inteiro, chamado pelo Android (MainActivity) e pelo iOS
 * (MainViewController). Espera as configurações do DataStore antes de
 * desenhar, para a primeira tela já sair no tema certo; [onSettingsLoaded]
 * avisa a plataforma (o Android segura o splash até lá).
 *
 * koinInject pega uma peça do Koin dentro de uma função @Composable.
 * collectAsState transforma o Flow das configurações num estado que, ao
 * mudar, recompõe a tela; remember guarda o Flow entre recomposições.
 * LaunchedEffect(Unit) roda o bloco uma vez quando a função entra na
 * composição. setSingletonImageLoaderFactory entrega o ImageLoader do Koin a
 * todo AsyncImage do app.
 */
@Composable
fun App(onSettingsLoaded: () -> Unit = {}) {
    val imageLoader = koinInject<ImageLoader>()
    setSingletonImageLoaderFactory { imageLoader }
    val settingsRepository = koinInject<SettingsRepository>()
    val settings by remember(settingsRepository) { settingsRepository.observe() }.collectAsState(initial = null)
    val loaded = settings ?: return
    LaunchedEffect(Unit) { onSettingsLoaded() }
    val darkTheme = loaded.theme.isDark(isSystemInDarkTheme())
    // TODO(frente-D): com a jornada ligada, as barras do sistema devem seguir o PiuTheme.isDark (o Kids é
    // sempre claro); hoje elas seguem só a preferência do adulto.
    SystemBarsAppearance(darkTheme)
    PiuTheme(themeMode = loaded.theme, dynamicColor = loaded.dynamicColors) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AppNavHost()
        }
    }
}

/**
 * Navegação de primeiro nível: as abas e as telas que abrem por cima delas.
 * Cada rota é uma classe @Serializable (Routes.kt), e os argumentos (o id da
 * espécie) vão nela. dropUnlessResumed só executa o "voltar" com a tela em
 * RESUMED; durante uma transição (entrando ou saindo) o toque é ignorado, o
 * que evita voltar duas vezes com toques rápidos. Os
 * blocos de rota só ligam as telas: os efeitos ficam dentro delas.
 */
@Composable
private fun AppNavHost() {
    val rootNav = rememberNavController()
    NavHost(rootNav, startDestination = TabsRoute) {
        composable<TabsRoute> {
            TabsScreen(
                onOpenDetail = { id -> rootNav.navigate(DetailRoute(id)) },
                onOpenStorage = { rootNav.navigate(StorageRoute) },
                onOpenAbout = { rootNav.navigate(AboutRoute) },
            )
        }
        composable<DetailRoute> {
            DetailRoot(onBack = dropUnlessResumed { rootNav.popBackStack() })
        }
        composable<StorageRoute> {
            StorageRoot(onBack = dropUnlessResumed { rootNav.popBackStack() })
        }
        composable<AboutRoute> {
            AboutRoot(
                onBack = dropUnlessResumed { rootNav.popBackStack() },
                onOpenLicenses = { rootNav.navigate(LicensesRoute) },
            )
        }
        composable<LicensesRoute> {
            LicensesRoot(onBack = dropUnlessResumed { rootNav.popBackStack() })
        }
    }
}
