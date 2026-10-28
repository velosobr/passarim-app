// Build do módulo app: o app compartilhado entre Android e iOS. É o único
// módulo que conhece todas as features e as liga (navegação e injeção de
// dependências). O plugin "passarim.ios-framework" empacota tudo como o
// framework que o projeto Xcode usa.

plugins {
    id("passarim.kmp-compose")
    id("passarim.serialization")
    id("passarim.ios-framework")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.presentation)
            implementation(projects.core.piuDs)
            implementation(projects.feature.explore.data)
            implementation(projects.feature.explore.presentation)
            implementation(projects.feature.detail.data)
            implementation(projects.feature.detail.presentation)
            implementation(projects.feature.favorites.presentation)
            implementation(projects.feature.settings.data)
            implementation(projects.feature.settings.presentation)
            implementation(libs.compose.resources)
            implementation(libs.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
