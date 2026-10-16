// Um texto da interface que pode ser pronto ou vir dos recursos traduzíveis.

package com.velosobr.passarim.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Texto que a UI mostra: ou uma string pronta ([DynamicString]) ou um recurso traduzível
 * ([Resource]). O ViewModel devolve [UiText]; só a UI sabe resolver para `String`.
 *
 * O ViewModel não pode ler recursos (isso exige estar dentro da composição),
 * então ele devolve a "receita" do texto, e a tela a resolve com [asString].
 */
sealed interface UiText {
    data class DynamicString(
        val value: String,
    ) : UiText

    class Resource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText

    /**
     * Resolve o texto. @Composable marca uma função que roda dentro da
     * composição (a montagem da tela) e por isso pode ler recursos.
     * "*args.toTypedArray()" espalha a lista como argumentos separados.
     */
    @Composable
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is Resource -> stringResource(id, *args.toTypedArray())
        }
}
