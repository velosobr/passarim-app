// Build do feature:explore:presentation: o ViewModel e as telas do Explorar.
// lifecycle-viewmodel-savedstate traz o SavedStateHandle, que guarda a busca e
// os filtros se o sistema matar o app em segundo plano.

plugins {
    id("passarim.kmp-compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.explore.domain)
            api(projects.core.presentation) // ExploreState expõe ScreenError
            implementation(projects.core.designSystem)
            implementation(libs.compose.resources)
            implementation(libs.lifecycle.viewmodel.savedstate)
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
