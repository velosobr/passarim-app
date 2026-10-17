// Build do módulo de snapshots: um módulo Android puro que fotografa as telas
// e os componentes com o Paparazzi (ADR-0010 e ADR-0017) e compara com as
// imagens de referência guardadas no repositório.

plugins {
    id("passarim.paparazzi")
}

android {
    namespace = "com.velosobr.passarim.snapshots"
}

dependencies {
    // Cada feature/*/presentation entra aqui, com `implementation` (os recursos precisam virar assets).
    implementation(projects.core.designSystem)
    implementation(projects.feature.explore.presentation)
    implementation(projects.feature.detail.presentation)
    implementation(projects.feature.favorites.presentation)
    implementation(projects.feature.settings.presentation)
    testImplementation(libs.junit4)
}
