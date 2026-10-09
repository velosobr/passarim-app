package com.velosobr.passarim.core.data

import io.ktor.client.engine.HttpClientEngine

/** Motor HTTP de cada plataforma: OkHttp (Android) e Darwin/NSURLSession (iOS). */
expect fun platformHttpEngine(): HttpClientEngine
