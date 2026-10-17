// Teste de migração do banco, a base para as migrações futuras.

package com.velosobr.passarim.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import assertk.assertThat
import assertk.assertions.isEqualTo
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.Test

/**
 * O MigrationTestHelper cria o banco a partir do esquema exportado
 * (schemas/), e não do código atual. Assim, quando houver a versão 2, o teste
 * cria a 1, aplica a migração e confere que os dados sobreviveram.
 */
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
