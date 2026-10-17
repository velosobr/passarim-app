package com.velosobr.passarim.feature.settings.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class AboutViewModel(
    versionName: String,
) : ViewModel() {
    val state: StateFlow<AboutState> = MutableStateFlow(AboutState(versionName)).asStateFlow()

    private val eventChannel = Channel<AboutEvent>(Channel.BUFFERED)
    val events: Flow<AboutEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: AboutAction) {
        val event =
            when (action) {
                AboutAction.Back -> AboutEvent.NavigateBack
                AboutAction.OpenPrivacyPolicy -> AboutEvent.OpenUrl(PRIVACY_POLICY_URL)
                AboutAction.OpenLicenses -> AboutEvent.NavigateToLicenses
            }
        eventChannel.trySend(event)
    }
}
