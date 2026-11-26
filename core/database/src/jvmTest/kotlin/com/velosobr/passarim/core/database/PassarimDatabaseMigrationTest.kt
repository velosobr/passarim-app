// Testes de migração do banco: criam uma versão antiga a partir do esquema
// exportado (schemas/), aplicam a migração e conferem os dados.

package com.velosobr.passarim.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.Test

/**
 * O MigrationTestHelper cria o banco a partir do esquema exportado, e não do
 * código atual. runMigrationsAndValidate aplica as migrações (aqui, a
 * automática da v1 para a v2, que o Room acha sozinho pela anotação no
 * PassarimDatabase) e confere que o resultado bate com o 2.json: se a
 * migração deixasse uma coluna diferente do esquema, o teste falharia aqui, e
 * não no aparelho de quem atualizou o app.
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
            connection.insertV1Favorite("bem-te-vi", "Bem-te-vi", "Pitangus sulphuratus", null, 1)
            assertThat(connection.longOf("SELECT COUNT(*) FROM favorites")).isEqualTo(1L)
        }
    }

    @Test
    fun `migra da 1 para a 2 com os favoritos de hoje no adulto e os mesmos dados`() {
        helper.createDatabase(1).use { connection ->
            connection.insertV1Favorite("bem-te-vi", "Bem-te-vi", "Pitangus sulphuratus", "https://media/bem-te-vi.webp", 10)
            connection.insertV1Favorite("tuiuiu", "Tuiuiú", "Jabiru mycteria", null, 20)
        }

        helper.runMigrationsAndValidate(2).use { connection ->
            val rows =
                connection
                    .prepare(
                        "SELECT id, commonName, scientificName, thumbnailUrl, addedAt, audience, primaryBiome FROM favorites ORDER BY id",
                    ).use { statement ->
                        buildList {
                            while (statement.step()) {
                                add(
                                    listOf(
                                        statement.getText(0),
                                        statement.getText(1),
                                        statement.getText(2),
                                        if (statement.isNull(3)) null else statement.getText(3),
                                        statement.getLong(4),
                                        statement.getText(5),
                                        if (statement.isNull(6)) null else statement.getText(6),
                                    ),
                                )
                            }
                        }
                    }
            assertThat(rows).containsExactly(
                listOf("bem-te-vi", "Bem-te-vi", "Pitangus sulphuratus", "https://media/bem-te-vi.webp", 10L, "ADULT", null),
                listOf("tuiuiu", "Tuiuiú", "Jabiru mycteria", null, 20L, "ADULT", null),
            )
        }
    }

    @Test
    fun `a versao 2 aceita a mesma ave no adulto e no Kids`() {
        helper.createDatabase(1).use { connection ->
            connection.insertV1Favorite("tuiuiu", "Tuiuiú", "Jabiru mycteria", null, 20)
        }

        helper.runMigrationsAndValidate(2).use { connection ->
            connection.execSQL(
                "INSERT INTO favorites (id, commonName, scientificName, thumbnailUrl, addedAt, audience, primaryBiome) " +
                    "VALUES ('tuiuiu', 'Tuiuiú', 'Jabiru mycteria', NULL, 30, 'KIDS', 'pantanal')",
            )
            assertThat(connection.longOf("SELECT COUNT(*) FROM favorites WHERE id = 'tuiuiu'")).isEqualTo(2L)
        }
    }

    @Test
    fun `a versao 2 cria a tabela de contagem dos mundos vazia`() {
        helper.createDatabase(1).close()

        helper.runMigrationsAndValidate(2).use { connection ->
            assertThat(connection.longOf("SELECT COUNT(*) FROM kids_world_counts")).isEqualTo(0L)
        }
    }

    /** Insere um favorito no formato da versão 1 (sem audiência nem bioma). */
    private fun SQLiteConnection.insertV1Favorite(
        id: String,
        commonName: String,
        scientificName: String,
        thumbnailUrl: String?,
        addedAt: Long,
    ) {
        prepare("INSERT INTO favorites (id, commonName, scientificName, thumbnailUrl, addedAt) VALUES (?, ?, ?, ?, ?)").use { statement ->
            statement.bindText(1, id)
            statement.bindText(2, commonName)
            statement.bindText(3, scientificName)
            if (thumbnailUrl == null) statement.bindNull(4) else statement.bindText(4, thumbnailUrl)
            statement.bindLong(5, addedAt)
            statement.step()
        }
    }

    /** Roda uma consulta que devolve um número só (um COUNT, por exemplo). */
    private fun SQLiteConnection.longOf(sql: String): Long =
        prepare(sql).use { statement ->
            statement.step()
            statement.getLong(0)
        }
}
