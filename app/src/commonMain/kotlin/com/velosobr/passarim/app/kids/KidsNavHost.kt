// A navegação do Passarim Kids: a casca com a barra do Kids e, por cima dela,
// a trava para adultos. É um NavHost separado do adulto: a raiz do app
// (App.kt) troca um pelo outro conforme a jornada gravada, e a pilha de um
// some junto com ele.

package com.velosobr.passarim.app.kids

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velosobr.passarim.app.navigation.KidsGateRoute
import com.velosobr.passarim.app.navigation.KidsTabsRoute
import com.velosobr.passarim.feature.journey.presentation.KidsGateRoot

/**
 * O NavHost do Kids. Os blocos de rota só ligam as telas: o "Para adultos" empilha a trava, e o
 * "Voltar" dela desempilha (dropUnlessResumed ignora um segundo toque durante a transição). O voltar
 * do sistema na trava também desempilha, sozinho; na casca, fecha o app sem mudar a jornada. O
 * acerto da trava não passa por aqui: grava ADULT, e a raiz troca de NavHost.
 */
@Composable
fun KidsNavHost() {
    val kidsNav = rememberNavController()
    NavHost(kidsNav, startDestination = KidsTabsRoute) {
        composable<KidsTabsRoute> {
            KidsTabsScreen(onOpenAdults = dropUnlessResumed { kidsNav.navigate(KidsGateRoute) })
        }
        composable<KidsGateRoute> {
            KidsGateRoot(onBack = dropUnlessResumed { kidsNav.popBackStack() })
        }
    }
}
