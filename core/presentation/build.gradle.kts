plugins {
    id("passarim.kmp-compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(libs.compose.runtime)
            api(libs.compose.resources)
            api(libs.lifecycle.runtime.compose)
            api(libs.lifecycle.viewmodel.compose)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
        }
    }
}
