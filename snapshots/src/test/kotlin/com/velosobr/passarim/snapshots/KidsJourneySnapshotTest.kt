// Snapshots do lado Kids da frente D, no tema Kids e nas três escalas de
// fonte: a casca (a barra do Kids com "Para adultos" e o Pipo) e a trava
// para adultos, com a pergunta e depois de um erro.

package com.velosobr.passarim.snapshots

import com.velosobr.passarim.app.kids.KidsTabsScreen
import com.velosobr.passarim.feature.journey.presentation.GateQuestion
import com.velosobr.passarim.feature.journey.presentation.KidsGateScreen
import com.velosobr.passarim.feature.journey.presentation.KidsGateState
import org.junit.Test

class KidsJourneySnapshotTest(
    fontScalePercent: Int,
) : KidsSnapshotTest(fontScalePercent) {
    @Test
    fun shell() = snap("kids-shell") { KidsTabsScreen(onOpenAdults = {}) }

    /** A trava com a conta da prancheta (13 × 7) e "91" digitado. */
    @Test
    fun gate() = snap("kids-gate") { KidsGateScreen(KidsGateState(GateQuestion(13, 7), answer = "91"), onAction = {}) }

    /** Depois de errar: a mensagem e a conta nova, sem algarismos. */
    @Test
    fun gateWrong() = snap("kids-gate-wrong") { KidsGateScreen(KidsGateState(GateQuestion(18, 6), wrongAnswer = true), onAction = {}) }
}
