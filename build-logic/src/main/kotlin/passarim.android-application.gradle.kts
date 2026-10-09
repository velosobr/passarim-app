// Casca Android fina: aplica o AGP (Kotlin embutido), o compilador do Compose e as versões de SDK.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin { jvmToolchain(21) }

ktlint {
    // Não valida o que o Gradle gera (acessores de recursos do Compose, por exemplo).
    filter { exclude { it.file.path.contains("/build/") } }
}
