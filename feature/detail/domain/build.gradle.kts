// Build do feature:detail:domain: os tipos e contratos do Detalhe, incluindo
// o contrato do player de áudio.

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
