// Gera o framework estático que o projeto Xcode (iosApp) consome.
plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "PassarimApp"
            isStatic = true
        }
    }
}
