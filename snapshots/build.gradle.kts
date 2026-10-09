plugins {
    id("passarim.paparazzi")
}

android {
    namespace = "com.velosobr.passarim.snapshots"
}

dependencies {
    // A partir da 5b, cada feature/*/presentation também entra aqui.
    implementation(projects.core.designSystem)
    testImplementation(libs.junit4)
}
