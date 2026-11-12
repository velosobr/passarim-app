// A chave da foto que viaja do card do mosaico (Explorar) para o topo do
// Detalhe. As duas telas ficam em módulos diferentes e precisam escrever a
// mesma chave; por isso ela mora aqui, no módulo que as duas já usam.

package com.velosobr.passarim.core.presentation

/** A chave do elemento compartilhado da foto de uma ave: "foto-<id>" (spec da frente E, seção 6). */
fun speciesPhotoKey(id: String): String = "foto-$id"
