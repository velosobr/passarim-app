// As regras de layout do mosaico adulto (frente E), num lugar só para as telas
// que o usam (Favoritos agora; o Explorar tem a mesma regra na própria feature).
// São funções puras: recebem números e devolvem números, sem Compose, e por
// isso são testadas como qualquer função comum (PiuMosaicLayoutTest).

package com.velosobr.piuds.components.adult

/**
 * A escala de fonte a partir da qual as telas adultas empilham o que ficaria
 * lado a lado (a grade vira uma coluna, as opções do tema vão uma embaixo da
 * outra). A regra é a da 5b: com fonte grande, empilhar, nunca cortar o texto.
 */
const val PIU_STACK_FONT_SCALE = 1.5f

/** Quantas colunas o mosaico tem para a escala de fonte do aparelho: 2, ou 1 com fonte grande. */
fun piuMosaicColumns(fontScale: Float): Int = if (fontScale >= PIU_STACK_FONT_SCALE) 1 else 2

/**
 * A altura do card na posição [index]: alto, baixo, baixo, alto, e repete. Com
 * duas colunas, a da esquerda começa alta e a da direita baixa (a grade põe
 * cada card na coluna mais curta), e as linhas nunca se alinham.
 */
fun piuMosaicHeightFor(index: Int): PiuMosaicHeight = if (index % 4 == 0 || index % 4 == 3) PiuMosaicHeight.TALL else PiuMosaicHeight.SHORT
