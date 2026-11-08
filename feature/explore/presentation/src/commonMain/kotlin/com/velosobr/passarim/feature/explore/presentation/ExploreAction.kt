// As ações que a tela Explorar manda ao ViewModel e os eventos de uma vez que
// ele devolve. É o padrão MVI: a tela só desenha o estado e avisa o que o
// usuário fez; quem decide o que acontecer é o ViewModel.

package com.velosobr.passarim.feature.explore.presentation

import com.velosobr.passarim.core.domain.SpeciesSummary

/** Tudo o que o usuário pode fazer no Explorar. */
sealed interface ExploreAction {
    /** O texto da busca mudou. */
    data class QueryChange(
        val text: String,
    ) : ExploreAction

    /** Chegou perto do fim da grade (veja [shouldLoadMore]). */
    data object LoadMore : ExploreAction

    /** "Tentar novamente" na linha de erro do fim da grade. */
    data object RetryLoadMore : ExploreAction

    /** "Tentar novamente" da tela de erro. */
    data object Retry : ExploreAction

    /** "Limpar filtros" do estado vazio: tira bioma e estado, mantém a busca. */
    data object ClearFilters : ExploreAction

    /** O X do chip selecionado. */
    data object ClearBiome : ExploreAction

    /** O X do chip de UF. */
    data object ClearState : ExploreAction

    /** Abre o sheet de filtros. */
    data object OpenFilters : ExploreAction

    /** Fecha o sheet sem aplicar. */
    data object DismissFilters : ExploreAction

    /** Seleciona o bioma no sheet; tocar no já selecionado desmarca. */
    data class SheetBiome(
        val code: String,
    ) : ExploreAction

    /** Seleciona a UF no sheet; tocar na já selecionada desmarca. */
    data class SheetState(
        val code: String,
    ) : ExploreAction

    /** "Ver todos os estados" do sheet. */
    data object ShowAllStates : ExploreAction

    /** "Limpar" do sheet: desmarca tudo no sheet. */
    data object ClearSheet : ExploreAction

    /** "Mostrar aves". */
    data object ApplyFilters : ExploreAction

    /** Tenta carregar os filtros de novo. */
    data object RetryFilters : ExploreAction

    /** O coração de um card. */
    data class ToggleFavorite(
        val summary: SpeciesSummary,
    ) : ExploreAction

    /** Toque num card. */
    data class OpenDetail(
        val id: String,
    ) : ExploreAction

    /** "Ver favoritos" da tela sem conexão. */
    data object OpenFavorites : ExploreAction

    /**
     * Toque num chip de bioma: [code] aplica o bioma; o mesmo bioma de novo, ou null ("Todos"),
     * tira o filtro. A UF escolhida no sheet continua.
     */
    data class SelectBiome(
        val code: String?,
    ) : ExploreAction

    /** Toque no card da "Ave do dia": abre o Detalhe. */
    data object OpenFeatured : ExploreAction

    /** Toque no botão de canto da "Ave do dia": abre o Detalhe já tocando o canto. */
    data object PlayFeatured : ExploreAction
}

/**
 * Eventos que acontecem uma vez (navegar): não fazem parte do estado, para
 * não se repetirem ao girar a tela.
 */
sealed interface ExploreEvent {
    /** Abrir o Detalhe da ave [id]; com [autoPlay], o canto começa ao abrir. */
    data class NavigateToDetail(
        val id: String,
        val autoPlay: Boolean = false,
    ) : ExploreEvent

    data object NavigateToFavorites : ExploreEvent
}
