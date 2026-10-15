package com.velosobr.passarim.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AboutViewModelTest {
    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `mostra a versao do app`() {
        assertThat(AboutViewModel(versionName = "1.0.0").state.value).isEqualTo(AboutState(versionName = "1.0.0"))
    }

    @Test
    fun `cada acao vira o seu evento`() =
        runTest {
            val vm = AboutViewModel(versionName = "1.0.0")

            vm.events.test {
                vm.onAction(AboutAction.OpenPrivacyPolicy)
                assertThat(awaitItem()).isEqualTo(AboutEvent.OpenUrl(PRIVACY_POLICY_URL))
                vm.onAction(AboutAction.OpenLicenses)
                assertThat(awaitItem()).isEqualTo(AboutEvent.NavigateToLicenses)
                vm.onAction(AboutAction.Back)
                assertThat(awaitItem()).isEqualTo(AboutEvent.NavigateBack)
            }
        }

    @Test
    fun `a politica de privacidade e um link https`() {
        assertThat(PRIVACY_POLICY_URL.startsWith("https://")).isEqualTo(true)
    }
}
