// O "contexto de plataforma" do Coil no iOS.

package com.velosobr.passarim.app.images

import coil3.PlatformContext
import org.koin.core.scope.Scope

/** No iOS, o Coil não precisa de contexto: usa a instância única. */
internal actual fun Scope.platformContext(): PlatformContext = PlatformContext.INSTANCE
