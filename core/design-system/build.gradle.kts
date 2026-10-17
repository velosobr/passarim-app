// Build do design system: tema, tokens e componentes visuais genéricos,
// usados por todas as features. Coil (o carregador de imagens) entra só como
// "implementation": os componentes de foto o usam por dentro.

plugins {
    id("passarim.kmp-compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.resources)
            implementation(libs.coil.compose)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
        }
    }
}
