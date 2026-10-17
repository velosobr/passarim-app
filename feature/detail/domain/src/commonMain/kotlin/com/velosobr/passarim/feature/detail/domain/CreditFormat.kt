// Formatação da licença e conferência dos links de crédito.

package com.velosobr.passarim.feature.detail.domain

/** VERSION_SUFFIX acha a versão no fim do código da licença ("-4.0"). */
private val VERSION_SUFFIX = Regex("-(\\d+(?:\\.\\d+)*)$")

/**
 * Deixa o código da licença legível: "CC-BY-NC-4.0" vira "CC BY-NC 4.0"
 * (o primeiro hífen e o da versão viram espaço). Vazio vira null.
 */
fun formatLicense(raw: String?): String? {
    val code = raw?.trim().orEmpty()
    if (code.isEmpty()) return null
    return VERSION_SUFFIX.replace(code.replaceFirst('-', ' ')) { " ${it.groupValues[1]}" }
}

/** O único esquema de link que o app abre. */
private const val HTTPS = "https://"

/**
 * Só abre links https, sem espaço no meio: o link vem de fora (do conteúdo das
 * fontes), e um "javascript:" ou um esquema estranho nunca deve ser aberto.
 */
fun isSafeLink(url: String?): Boolean =
    url != null &&
        url.startsWith(HTTPS, ignoreCase = true) &&
        url.length > HTTPS.length &&
        url.none { it.isWhitespace() }
