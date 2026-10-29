// Build do PiuDS, o design system do Passarim (variantes Piu e Piu Kids):
// tema, tokens e componentes visuais usados por todas as features. Coil (o
// carregador de imagens) entra só como "implementation": os componentes de
// foto o usam por dentro. O PiuDS usa o pacote curto com.velosobr.piuds, e
// não o padrão por caminho do convention plugin, por isso o namespace
// Android e o pacote dos recursos são fixados aqui.

plugins {
    id("passarim.kmp-compose")
}

kotlin {
    android {
        namespace = "com.velosobr.piuds"
    }
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

compose.resources {
    packageOfResClass = "com.velosobr.piuds.resources"
}
