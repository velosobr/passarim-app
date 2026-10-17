// O player de áudio no iOS.

package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.feature.detail.domain.AudioPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

/** factory cria um player novo para cada Detalhe aberto. */
actual val audioPlayerModule: Module =
    module {
        factory<AudioPlayer> { AvAudioPlayer() }
    }
