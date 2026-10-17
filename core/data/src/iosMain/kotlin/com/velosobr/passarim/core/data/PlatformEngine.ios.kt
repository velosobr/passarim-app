// Motor HTTP do iOS.

package com.velosobr.passarim.core.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

/** No iOS, o motor é o Darwin, que usa o NSURLSession do sistema. */
actual fun platformHttpEngine(): HttpClientEngine = Darwin.create()
