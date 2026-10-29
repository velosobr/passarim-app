// Os tipos das tabelas de tipografia dos tokens: uma linha por estilo do
// Material 3. A tabela do Piu fica em Type.kt e a do Kids em KidsType.kt;
// os testes leem as tabelas para conferir os valores contra os tokens.

package com.velosobr.piuds.theme

/** Um estilo da tabela "type.styles" dos tokens: tamanho e linha em sp, peso e espaçamento entre letras (sp). */
data class PiuTypeSpec(
    val size: Int,
    val line: Int,
    val weight: Int,
    val tracking: Float,
)

/** Um estilo da tabela "kids.type.styles": família (Fredoka ou Nunito), tamanho e linha em sp, e peso. */
data class KidsTypeSpec(
    val fredoka: Boolean,
    val size: Int,
    val line: Int,
    val weight: Int,
)
