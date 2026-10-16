// Troca do host das URLs de mídia em debug.

package com.velosobr.passarim.core.data

import com.velosobr.passarim.core.domain.AppConfig
import io.ktor.http.URLBuilder
import io.ktor.http.Url

/**
 * Em debug, o BFF devolve URLs de mídia com `localhost` (a porta do SeaweedFS no computador), que o
 * emulador e o aparelho não alcançam. Este conversor troca **só o host** por [AppConfig.mediaHostOverride]
 * (por exemplo `10.0.2.2` no emulador), mantendo porta, caminho e query. Em release o override é nulo e
 * nada muda; hosts que não são locais nunca são reescritos.
 */
class MediaUrlRewriter(
    private val config: AppConfig,
) {
    /** Devolve a URL com o host trocado, ou a mesma URL quando não há o que trocar. */
    fun rewrite(url: String?): String? {
        val host = config.mediaHostOverride ?: return url
        // O parser do Ktor aceita qualquer texto como caminho relativo em `localhost`; só reescrevemos URLs absolutas.
        val parsed = url?.takeIf { it.startsWith("http://") || it.startsWith("https://") }?.let(::parse)
        return if (url != null && parsed != null && parsed.host in LOCAL_HOSTS) {
            URLBuilder(parsed).apply { this.host = host }.buildString()
        } else {
            url
        }
    }

    /** Interpreta a URL; texto inválido vira null em vez de exceção. */
    private fun parse(url: String): Url? =
        try {
            Url(url)
        } catch (_: Exception) {
            null
        }

    /** "companion object" guarda o que pertence à classe, e não a cada objeto: aqui, os hosts locais. */
    private companion object {
        val LOCAL_HOSTS = setOf("localhost", "127.0.0.1")
    }
}
