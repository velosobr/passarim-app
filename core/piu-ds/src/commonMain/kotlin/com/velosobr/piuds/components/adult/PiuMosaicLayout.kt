// As regras de layout do mosaico adulto (frente E), num lugar só para as telas
// que o usam (Explorar e Favoritos): quantas colunas, a altura de cada card e o
// bioma do gradiente quando a ave não tem um. São funções puras: recebem números
// e devolvem números, sem Compose, e por isso são testadas como qualquer função
// comum (PiuMosaicLayoutTest).

package com.velosobr.piuds.components.adult

import com.velosobr.passarim.core.domain.Biome

/**
 * A escala de fonte a partir da qual as telas adultas empilham o que ficaria
 * lado a lado (a grade vira uma coluna, as opções do tema vão uma embaixo da
 * outra). A regra é a da 5b: com fonte grande, empilhar, nunca cortar o texto.
 */
const val PIU_STACK_FONT_SCALE = 1.5f

/** Quantas colunas o mosaico tem para a escala de fonte do aparelho: 2, ou 1 com fonte grande. */
fun piuMosaicColumns(fontScale: Float): Int = if (fontScale >= PIU_STACK_FONT_SCALE) 1 else 2

/**
 * A altura do card na posição [index]: alto, baixo, alto, baixo, e repete.
 *
 * A grade (LazyVerticalStaggeredGrid) põe cada card na coluna mais curta naquele
 * momento, então a ordem dos cards não é "esquerda, direita, esquerda...":
 * - 0, alto: vai para a esquerda (as duas vazias, ganha a primeira);
 * - 1, baixo: vai para a direita, a vazia;
 * - 2, alto: vai para a direita de novo, que ficou mais curta (baixo < alto);
 * - 3, baixo: vai para a esquerda, que agora é a mais curta.
 *
 * Resultado: a esquerda fica alta-baixa, a direita baixa-alta, as linhas nunca
 * se alinham e as duas colunas terminam juntas a cada quatro cards, como na
 * prancheta. Por isso a sequência é esta e não "alto, baixo, baixo, alto", que
 * a spec da frente E cita: aquela deixaria os dois altos na mesma coluna.
 */
fun piuMosaicHeightFor(index: Int): PiuMosaicHeight = if (index % 2 == 0) PiuMosaicHeight.TALL else PiuMosaicHeight.SHORT

/** O gradiente de um card sem bioma principal (o catalog nem sempre traz esse dado): o do Pantanal. */
val PiuFallbackBiome = Biome.PANTANAL
