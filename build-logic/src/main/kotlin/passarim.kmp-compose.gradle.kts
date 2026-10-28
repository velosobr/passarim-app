// Biblioteca KMP com Compose Multiplatform e recursos (strings, fontes, drawables).
plugins {
    id("passarim.kmp-library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    android {
        // O Paparazzi só enxerga os recursos do Compose se eles virarem assets Android.
        androidResources { enable = true }
    }
}

compose.resources {
    publicResClass = true
    // ":core:domain" -> "com.velosobr.passarim.core.domain.resources"
    packageOfResClass = "com.velosobr.passarim" + project.path.replace(":", ".").replace("-", "") + ".resources"
}
