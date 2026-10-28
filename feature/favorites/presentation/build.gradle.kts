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
    }
}
