// Build do feature:detail:presentation: o ViewModel, as telas, o mapa
// (MapLibre Compose) e o player. No Android, o player é o ExoPlayer (Media3) e
// o mapa precisa do runtime OpenGL do MapLibre.

plugins {
    id("passarim.kmp-compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.detail.domain)
            api(projects.core.presentation)
            implementation(projects.core.piuDs)
            implementation(libs.compose.resources)
            implementation(libs.lifecycle.viewmodel.savedstate)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.maplibre.compose)
        }
        androidMain.dependencies {
            implementation(libs.media3.exoplayer)
            implementation(libs.maplibre.runtime.opengl)
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
        // Testes de Compose no host Android (Robolectric): montam o player e o topo do Detalhe e
        // consultam a árvore de semântica (o que o leitor de tela lê e os tamanhos de toque).
        getByName("androidHostTest").dependencies {
            implementation(libs.compose.uiTest)
            implementation(libs.androidx.compose.uiTestManifest)
            implementation(libs.robolectric)
            implementation(libs.junit4)
        }
    }
}
