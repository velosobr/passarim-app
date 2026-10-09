package com.velosobr.passarim.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Texto que a UI mostra: ou uma string pronta ([DynamicString]) ou um recurso traduzível
 * ([Resource]). O ViewModel devolve [UiText]; só a UI sabe resolver para `String`.
 */
sealed interface UiText {
    data class DynamicString(
        val value: String,
    ) : UiText

    class Resource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    @Composable
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is Resource -> stringResource(id, *args.toTypedArray())
        }
}
