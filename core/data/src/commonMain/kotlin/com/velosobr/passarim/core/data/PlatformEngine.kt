// Declaração do motor HTTP de cada plataforma.

package com.velosobr.passarim.core.data

import io.ktor.client.engine.HttpClientEngine

/**
 * Motor HTTP de cada plataforma: OkHttp (Android) e Darwin/NSURLSession (iOS).
 *
 * "expect" declara a função no código comum sem corpo; cada plataforma dá a
 * sua versão com "actual" (os arquivos PlatformEngine.android.kt e
 * PlatformEngine.ios.kt). É o jeito do KMP de usar algo diferente em cada
 * sistema.
 */
expect fun platformHttpEngine(): HttpClientEngine
