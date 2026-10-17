// Build do core:database: o banco local (Room KMP, ADR-0009) com os favoritos.
// O plugin "passarim.room" liga o KSP, que gera o código do Room.

plugins {
    id("passarim.kmp-library")
    id("passarim.room")
}

kotlin {
    // O alvo JVM existe para os testes do banco rodarem no computador
    // (jvmTest), com o SQLite embutido, sem emulador.
    jvm()

    compilerOptions {
        // Classes expect/actual (o PassarimDatabaseConstructor) ainda são "beta" no
        // Kotlin; esta opção confirma o uso e cala o aviso do compilador.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            implementation(libs.room.runtime)
            // sqlite-bundled traz o próprio SQLite, igual em todas as plataformas.
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        jvmTest.dependencies {
            implementation(libs.room.testing)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.assertk)
        }
    }
}

dependencies {
    // KSP (Kotlin Symbol Processing) lê as anotações do Room (@Entity, @Dao...) e
    // gera o código do banco, um alvo por vez.
    listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64", "kspJvm").forEach { add(it, libs.room.compiler) }
}
