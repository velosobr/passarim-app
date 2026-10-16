// Motor HTTP do Android.

package com.velosobr.passarim.core.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

/** No Android, o motor é o OkHttp. */
actual fun platformHttpEngine(): HttpClientEngine = OkHttp.create()
