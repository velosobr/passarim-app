// Build do feature:explore:domain: os contratos da tela Explorar, sem
// dependência de rede nem de interface.

plugins {
    id("passarim.kmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
        }
    }
}
