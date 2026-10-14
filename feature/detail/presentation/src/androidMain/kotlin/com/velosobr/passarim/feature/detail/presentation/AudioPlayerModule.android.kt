package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.feature.detail.domain.AudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val audioPlayerModule: Module =
    module {
        factory<AudioPlayer> { ExoAudioPlayer(androidContext()) }
    }
