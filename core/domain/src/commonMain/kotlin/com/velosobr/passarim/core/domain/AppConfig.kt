// Configuração de execução do app.

package com.velosobr.passarim.core.domain

/**
 * Configuração de execução, preenchida por cada plataforma (BuildConfig no Android, xcconfig no iOS).
 * Os módulos KMP são compilados uma vez só e não conhecem build types; por isso debug/release chega aqui.
 */
data class AppConfig(
    val isDebug: Boolean,
    val baseUrl: String,
    /** Só em debug: host alcançável pela plataforma para trocar o `localhost` das URLs de mídia. */
    val mediaHostOverride: String?,
    val versionName: String,
)
