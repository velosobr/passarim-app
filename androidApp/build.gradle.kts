// Build do app Android: a "casca" que empacota o módulo app (o app
// compartilhado) num APK/AAB, com o ícone, a splash e a configuração por
// build type (debug e release).

import java.util.Properties

plugins {
    id("passarim.android-application")
    id("passarim.licenses")
}

// Endereço do BFF no debug. Padrão: o emulador Android alcança o computador por 10.0.2.2.
// Para um aparelho físico, crie `local.properties` com `bff.baseUrl=http://<IP-do-computador>:8080`.
val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }
val debugBaseUrl: String = localProperties.getProperty("bff.baseUrl", "http://10.0.2.2:8080")

android {
    namespace = "com.velosobr.passarim"

    defaultConfig {
        applicationId = "com.velosobr.passarim"
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"$debugBaseUrl\"")
        }
        release {
            // R8 encolhe e ofusca o código do release (tira o que não é usado).
            // TODO: completar as regras de keep do R8 antes de publicar.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Endereço reservado e inválido: o release nunca fala com o ambiente local.
            // TODO: trocar pelo endereço real do BFF no deploy.
            buildConfigField("String", "BASE_URL", "\"https://api.passarim.invalid\"")
        }
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.core.domain)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.android)
    // "constraints" não acrescenta dependência: só diz qual versão usar se
    // alguém a trouxer. O media3 (player do canto) traz o Guava 33.3.1, que tem
    // a GHSA-xxph-c9ww-hj94 (CVSS 8.2, alocação de memória sem limite ao
    // desserializar); a 33.7.2 corrige.
    constraints {
        implementation(libs.guava) {
            because("GHSA-xxph-c9ww-hj94 no Guava até 33.7.1")
        }
    }
}

// Exporta a lista de licenças das bibliotecas do release para o JSON que a
// tela de Licenças lê (ADR-0020).
aboutLibraries {
    export {
        variant = "release"
        outputFile = rootProject.file("feature/settings/presentation/src/commonMain/composeResources/files/aboutlibraries.json")
        prettyPrint = true
    }
}
