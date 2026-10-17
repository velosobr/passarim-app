// Build do core:data: a camada de rede (Ktor) e o JSON (kotlinx.serialization).

plugins {
    id("passarim.kmp-library")
    id("passarim.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }
        // Cada plataforma tem o seu "motor" HTTP: OkHttp no Android, Darwin
        // (NSURLSession) no iOS. O código comum só conhece o HttpClient do Ktor.
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        // ktor-client-mock deixa os testes responderem como se fossem o BFF, sem rede.
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.assertk)
        }
    }
}
