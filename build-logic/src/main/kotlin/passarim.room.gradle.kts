// Room: o KSP gera o código do banco, e o plugin do Room exporta o esquema
// de cada versão em schemas/ (usado pelos testes de migração).

plugins {
    id("com.google.devtools.ksp")
    id("androidx.room")
}

room {
    schemaDirectory("$projectDir/schemas")
}
