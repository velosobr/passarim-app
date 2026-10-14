package com.velosobr.passarim.feature.detail.domain

private val VERSION_SUFFIX = Regex("-(\\d+(?:\\.\\d+)*)$")

fun formatLicense(raw: String?): String? {
    val code = raw?.trim().orEmpty()
    if (code.isEmpty()) return null
    return VERSION_SUFFIX.replace(code.replaceFirst('-', ' ')) { " ${it.groupValues[1]}" }
}

private const val HTTPS = "https://"

fun isSafeLink(url: String?): Boolean =
    url != null &&
        url.startsWith(HTTPS, ignoreCase = true) &&
        url.length > HTTPS.length &&
        url.none { it.isWhitespace() }
