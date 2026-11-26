// Snapshots da Escolha de jornada (frente D), no tema do adulto (claro e
// escuro) e nas três escalas de fonte, com o cartão Kids em cada um dos
// quatro estados. A prévia do cartão Kids é sempre clara.

package com.velosobr.passarim.snapshots

import com.velosobr.passarim.core.presentation.KidsAvailabilityState
import com.velosobr.passarim.feature.journey.presentation.JourneyChoiceScreen
import com.velosobr.passarim.feature.journey.presentation.JourneyChoiceState
import org.junit.Test

class JourneySnapshotTest(
    dark: Boolean,
    fontScalePercent: Int,
) : ThemedSnapshotTest(dark, fontScalePercent) {
    private fun choice(
        name: String,
        kids: KidsAvailabilityState,
    ) = snap(name) { JourneyChoiceScreen(JourneyChoiceState(kids), onAction = {}) }

    @Test
    fun checking() = choice("journey-choice-checking", KidsAvailabilityState.CHECKING)

    @Test
    fun available() = choice("journey-choice-available", KidsAvailabilityState.AVAILABLE)

    @Test
    fun offline() = choice("journey-choice-offline", KidsAvailabilityState.OFFLINE)

    @Test
    fun hidden() = choice("journey-choice-hidden", KidsAvailabilityState.HIDDEN)
}
