// Build do feature:journey:presentation: a Escolha de jornada (Passarim ou
// Passarim Kids) e a trava para adultos, que guarda a saída do Kids (frente D).
// Não tem módulos de domain e data próprios: a jornada é gravada pelo
// SettingsRepository e a checagem do Kids vem do KidsAvailabilityRepository,
// os dois contratos do core:domain.

plugins {
    id("passarim.kmp-compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(projects.core.presentation)
            implementation(projects.core.piuDs)
            implementation(libs.compose.resources)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
        // Testes de Compose no host Android (como no PiuDS): o Robolectric imita o Android numa JVM
        // comum, e o ui-test monta a tela e consulta a árvore de semântica (o que o leitor de tela lê).
        getByName("androidHostTest").dependencies {
            implementation(libs.compose.uiTest)
            implementation(libs.androidx.compose.uiTestManifest)
            implementation(libs.robolectric)
            implementation(libs.junit4)
        }
    }
}
