// Build do core:domain. O plugin "passarim.kmp-library" (em build-logic)
// aplica a configuração comum dos módulos KMP: Android, iOS e as versões.

plugins {
    id("passarim.kmp-library")
}

kotlin {
    // Alvo JVM a mais: o Room gera código para o alvo JVM do core:database, que
    // depende deste módulo, então o domínio também precisa existir para a JVM.
    jvm()

    sourceSets {
        commonMain.dependencies {
            // "api" (e não "implementation") expõe as coroutines a quem depende do
            // domínio, porque as interfaces daqui usam Flow nas assinaturas.
            api(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.assertk)
        }
    }
}
