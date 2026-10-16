// Build do core:presentation: o que todas as telas compartilham (textos,
// erros de tela, eventos). O plugin "passarim.kmp-compose" liga o Compose
// Multiplatform. As dependências são "api" porque as features usam esses
// tipos (ViewModel, StringResource) direto.

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
