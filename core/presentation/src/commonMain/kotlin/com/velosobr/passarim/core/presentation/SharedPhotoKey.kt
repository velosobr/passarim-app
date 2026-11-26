// As chaves da foto que viaja de um card do Explorar (o do mosaico ou o da
// "Ave do dia") para o topo do Detalhe. As duas telas ficam em módulos
// diferentes e precisam escrever a mesma chave; por isso elas moram aqui, no
// módulo que as duas já usam.

package com.velosobr.passarim.core.presentation

/** A chave do elemento compartilhado da foto de uma ave no mosaico: "foto-<id>" (spec da frente E, seção 6). */
fun speciesPhotoKey(id: String): String = "foto-$id"

/**
 * A chave da foto do card da "Ave do dia": "foto-destaque-<id>". É outra que a do mosaico porque a
 * mesma ave costuma estar nos dois cards ao mesmo tempo; com uma chave só, o Compose não saberia de
 * qual dos dois a foto parte, e ela poderia sair do card que ninguém tocou.
 */
fun featuredPhotoKey(id: String): String = "foto-destaque-$id"
