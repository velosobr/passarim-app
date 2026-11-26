// A raiz do app: tema, carregador de imagens e a navegação de primeiro nível.
// A jornada gravada (frente D) escolhe o que abre: a Escolha, o adulto ou o
// Kids, cada um com o próprio NavHost.

package com.velosobr.passarim.app

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
import com.velosobr.passarim.app.kids.KidsNavHost
import com.velosobr.passarim.app.navigation.AboutRoute
import com.velosobr.passarim.app.navigation.DetailRoute
import com.velosobr.passarim.app.navigation.LicensesRoute
import com.velosobr.passarim.app.navigation.StorageRoute
import com.velosobr.passarim.app.navigation.TabsRoute
import com.velosobr.passarim.app.navigation.toRoute
import com.velosobr.passarim.app.tabs.TabsScreen
import com.velosobr.passarim.app.theme.SystemBarsAppearance
import com.velosobr.passarim.core.domain.Journey
import com.velosobr.passarim.core.domain.Settings
import com.velosobr.passarim.core.domain.SettingsRepository
import com.velosobr.passarim.feature.detail.presentation.DetailRoot
import com.velosobr.passarim.feature.journey.presentation.JourneyChoiceRoot
import com.velosobr.passarim.feature.settings.presentation.AboutRoot
import com.velosobr.passarim.feature.settings.presentation.LicensesRoot
import com.velosobr.passarim.feature.settings.presentation.StorageRoot
import com.velosobr.piuds.components.PiuSharedScope
import com.velosobr.piuds.components.PiuSharedTransitionLayout
import com.velosobr.piuds.theme.PiuTheme
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
    JourneyRoot(
        settings = loaded,
        choice = { JourneyChoiceRoot() },
        adult = { AppNavHost() },
        kids = { KidsNavHost() },
    )
}

/**
 * O tema e a tela de cada jornada. O DataStore é a única fonte da jornada: trocar de jornada é só
 * gravar outra (setJourney), e este "when" recompõe com o outro conteúdo; o NavHost que sai leva a
 * pilha dele junto, sem navigate nem popUpTo entre jornadas.
 * - journey nula (primeira abertura): [choice], a Escolha, no tema do adulto;
 * - ADULT: [adult], as abas e as telas por cima;
 * - KIDS: [kids], a casca do Kids e a trava.
 *
 * O PiuTheme recebe a jornada: no Kids ele ignora o tema e as cores dinâmicas gravados, sem mudá-los.
 * SystemBarsAppearance fica dentro do tema para ler o PiuTheme.isDark efetivo: no Kids os ícones das
 * barras ficam escuros mesmo com o adulto no tema escuro. As três telas entram como parâmetros
 * (slots) para o teste da raiz trocá-las por textos simples, sem o Koin e a rede.
 */
@Composable
internal fun JourneyRoot(
    settings: Settings,
    choice: @Composable () -> Unit,
    adult: @Composable () -> Unit,
    kids: @Composable () -> Unit,
) {
    PiuTheme(journey = settings.journey ?: Journey.ADULT, themeMode = settings.theme, dynamicColor = settings.dynamicColors) {
        SystemBarsAppearance(PiuTheme.isDark)
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (settings.journey) {
                null -> choice()
                Journey.ADULT -> adult()
                Journey.KIDS -> kids()
            }
        }
    }
}

/**
 * Navegação de primeiro nível do adulto: as abas e as telas que abrem por cima delas.
 * Cada rota é uma classe @Serializable (Routes.kt), e os argumentos (o id da
 * espécie) vão nela. dropUnlessResumed só executa o "voltar" com a tela em
 * RESUMED; durante uma transição (entrando ou saindo) o toque é ignorado, o
 * que evita voltar duas vezes com toques rápidos. Os
 * blocos de rota só ligam as telas: os efeitos ficam dentro delas.
 *
 * PiuSharedTransitionLayout em volta do NavHost deixa a foto do card do
 * mosaico viajar até o topo do Detalhe. Cada bloco de rota passa o próprio
 * escopo de animação (o `this` de composable { }) com PiuSharedScope; as
 * abas, que têm o NavHost delas, usam o escopo da rota das abas daqui.
 */
@Composable
private fun AppNavHost() {
    val rootNav = rememberNavController()
    PiuSharedTransitionLayout {
        NavHost(rootNav, startDestination = TabsRoute) {
            composable<TabsRoute> {
                PiuSharedScope(this) {
                    TabsScreen(
                        onOpenDetail = { opening -> rootNav.navigate(opening.toRoute()) },
                        onOpenStorage = { rootNav.navigate(StorageRoute) },
                        onOpenAbout = { rootNav.navigate(AboutRoute) },
                    )
                }
            }
            composable<DetailRoute> {
                PiuSharedScope(this) { DetailRoot(onBack = dropUnlessResumed { rootNav.popBackStack() }) }
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
}
