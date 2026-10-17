package com.velosobr.passarim.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import assertk.assertThat
import assertk.assertions.isEqualTo
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.Test

class PassarimDatabaseMigrationTest {
    private val databaseFile = Files.createTempDirectory("passarim-db").resolve("passarim.db")

    private val helper =
        MigrationTestHelper(
            schemaDirectoryPath = Path("schemas"),
            databasePath = databaseFile,
            driver = BundledSQLiteDriver(),
            databaseClass = PassarimDatabase::class,
        )

    @Test
    fun `cria a versao 1 a partir do esquema exportado`() {
        helper.createDatabase(1).use { connection ->
            connection.execSQL(
                "INSERT INTO favorites (id, commonName, scientificName, thumbnailUrl, addedAt) " +
                    "VALUES ('bem-te-vi', 'Bem-te-vi', 'Pitangus sulphuratus', NULL, 1)",
            )
            connection.prepare("SELECT COUNT(*) FROM favorites").use { statement ->
                statement.step()
                assertThat(statement.getLong(0)).isEqualTo(1L)
            }
        }
    }
}
