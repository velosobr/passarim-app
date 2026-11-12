// Build do feature:favorites:presentation: a aba Favoritos. Não tem módulos de
// domain e data próprios: usa o FavoritesRepository do core (Room).

plugins {
    id("passarim.kmp-compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(projects.core.presentation)
            implementation(projects.core.piuDs)
            implementation(libs.compose.resources)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
        // Testes de Compose no host Android (como no PiuDS): o Robolectric imita o Android numa JVM
        // comum, e o ui-test monta a tela e consulta a árvore de semântica (o que o leitor de tela lê).
        getByName("androidHostTest").dependencies {
            implementation(libs.compose.uiTest)
            implementation(libs.androidx.compose.uiTestManifest)
            implementation(libs.robolectric)
            implementation(libs.junit4)
        }
    }
}
