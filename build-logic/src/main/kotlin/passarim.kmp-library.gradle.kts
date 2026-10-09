// Módulo KMP de biblioteca: Android (plugin novo do AGP 9) + iOS, com testes de commonTest
// rodando na JVM do host (testAndroidHostTest) e no simulador iOS.
plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    android {
        // ":core:design-system" -> "com.velosobr.passarim.core.designsystem"
        namespace = "com.velosobr.passarim" + project.path.replace(":", ".").replace("-", "")
        compileSdk = 37
        minSdk = 26
        withHostTest { }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

ktlint {
    // Não valida o que o Gradle gera (acessores de recursos do Compose, por exemplo).
    filter { exclude { it.file.path.contains("/build/") } }
}
