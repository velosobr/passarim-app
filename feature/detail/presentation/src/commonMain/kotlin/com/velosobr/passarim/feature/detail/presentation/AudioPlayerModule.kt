// Declaração do módulo Koin do player, que cada plataforma implementa.

package com.velosobr.passarim.feature.detail.presentation

import org.koin.core.module.Module

/** Cada plataforma registra o seu AudioPlayer (AudioPlayerModule.android.kt e .ios.kt). */
expect val audioPlayerModule: Module
