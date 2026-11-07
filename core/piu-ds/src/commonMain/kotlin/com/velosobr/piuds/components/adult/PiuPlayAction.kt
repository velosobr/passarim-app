// O botão de canto que um card pode ter (hoje, a "Ave do dia"). Agrupa o que
// o botão precisa num tipo só para que a descrição seja obrigatória: um
// botão só com ícone, sem descrição, fica mudo para o leitor de tela
// (README do PiuDS, seção 6).

package com.velosobr.piuds.components.adult

/**
 * O canto do card. [description] é o que o leitor de tela diz do botão
 * ("Ouvir o canto do Tuiuiú"); [playing] troca o ícone de tocar pelo de
 * pausar; [onClick] toca ou pausa.
 */
data class PiuPlayAction(
    val description: String,
    val playing: Boolean,
    val onClick: () -> Unit,
)
