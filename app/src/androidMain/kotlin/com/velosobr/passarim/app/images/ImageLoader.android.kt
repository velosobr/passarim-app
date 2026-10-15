package com.velosobr.passarim.app.images

import coil3.PlatformContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.platformContext(): PlatformContext = androidContext()
