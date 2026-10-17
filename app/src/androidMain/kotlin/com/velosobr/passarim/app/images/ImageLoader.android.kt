// O "contexto de plataforma" que o Coil precisa, no Android.

package com.velosobr.passarim.app.images

import coil3.PlatformContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

/** No Android, o Coil usa o Context do app, que o Koin fornece. */
internal actual fun Scope.platformContext(): PlatformContext = androidContext()
