// Build do PiuDS, o design system do Passarim (variantes Piu e Piu Kids):
// tema, tokens e componentes visuais usados por todas as features. Coil (o
// carregador de imagens) entra só como "implementation": os componentes de
// foto o usam por dentro. O PiuDS usa o pacote curto com.velosobr.piuds, e
// não o padrão por caminho do convention plugin, por isso o namespace
// Android e o pacote dos recursos são fixados aqui. Haze (desfoque) e
// graphics-shapes (polígonos arredondados) entram no plano C2, só para os
// componentes: o Haze fica isolado no PiuBlurSurface (ADR-0023).

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
            implementation(libs.haze)
            implementation(libs.haze.blur)
            implementation(libs.graphics.shapes)
            // A anotação @Preview, para ver cada componente no painel do Android Studio.
            implementation(libs.compose.uiToolingPreview)
            // Para reler a preferência "reduzir animações" quando a pessoa volta ao app.
            implementation(libs.lifecycle.runtime.compose)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
        }
        // Testes de Compose no host Android: o Robolectric imita o Android numa JVM comum, e o
        // ui-test monta a composição e consulta a árvore de semântica (o que o leitor de tela lê).
        getByName("androidHostTest").dependencies {
            implementation(libs.compose.uiTest)
            implementation(libs.androidx.compose.uiTestManifest)
            implementation(libs.robolectric)
            implementation(libs.junit4)
        }
    }
}

compose.resources {
    packageOfResClass = "com.velosobr.piuds.resources"
}
