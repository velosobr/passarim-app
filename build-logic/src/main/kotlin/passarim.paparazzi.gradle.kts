// Módulo Android puro só para snapshot tests (Paparazzi). Não é KMP: o Paparazzi só roda em Android,
// e fica fora dos módulos de produto para não arrastar bibliotecas nativas para eles.
plugins {
    id("com.android.library")
    id("app.cash.paparazzi")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// O Paparazzi 2.x gera bytecode Java 21: os testes precisam rodar em JDK 21.
kotlin { jvmToolchain(21) }

ktlint {
    // Não valida o que o Gradle gera (acessores de recursos do Compose, por exemplo).
    filter { exclude { it.file.path.contains("/build/") } }
}
