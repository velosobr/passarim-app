plugins {
    id("passarim.kmp-library")
}

kotlin {
    // Alvo só para testes: o Room KMP com BundledSQLiteDriver roda na JVM do host (jvmTest).
    // Nada de produção usa este alvo. As entidades e o Room entram na etapa 5d.
    jvm()
}
