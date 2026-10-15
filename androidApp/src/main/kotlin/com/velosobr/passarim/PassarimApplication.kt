package com.velosobr.passarim

import android.app.Application
import com.velosobr.passarim.app.di.initKoin
import com.velosobr.passarim.core.domain.AppConfig
import org.koin.android.ext.koin.androidContext
import java.net.URI

class PassarimApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(config()) { androidContext(this@PassarimApplication) }
    }

    /** Debug/release vêm do `BuildConfig`, porque os módulos KMP não conhecem build types. */
    private fun config() =
        AppConfig(
            isDebug = BuildConfig.DEBUG,
            baseUrl = BuildConfig.BASE_URL,
            // No debug, a mídia local (`localhost`) é reescrita para o mesmo host do BFF (10.0.2.2 no emulador).
            mediaHostOverride = if (BuildConfig.DEBUG) URI(BuildConfig.BASE_URL).host else null,
            versionName = BuildConfig.VERSION_NAME,
        )
}
