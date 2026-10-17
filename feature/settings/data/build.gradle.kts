// Build do feature:settings:data: as preferências no DataStore e a limpeza
// do cache de imagens do Coil.

plugins {
    id("passarim.kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            implementation(libs.datastore.preferences.core)
            implementation(libs.coil.core)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.assertk)
        }
    }
}
