package com.velosobr.passarim.app.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InMemorySettingsRepositoryTest {
    @Test
    fun `comeca com os padroes e reproducao automatica desligada`() =
        runTest {
            assertThat(InMemorySettingsRepository().observe().first()).isEqualTo(Settings(autoPlay = false))
        }
}
