package com.velosobr.passarim.feature.detail.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlin.test.Test

class CreditFormatTest {
    @Test
    fun `formatLicense separa o prefixo e a versao`() {
        assertThat(formatLicense("CC0")).isEqualTo("CC0")
        assertThat(formatLicense("CC-BY")).isEqualTo("CC BY")
        assertThat(formatLicense("CC-BY-NC")).isEqualTo("CC BY-NC")
        assertThat(formatLicense("CC-BY-4.0")).isEqualTo("CC BY 4.0")
        assertThat(formatLicense("CC-BY-NC-SA-4.0")).isEqualTo("CC BY-NC-SA 4.0")
    }

    @Test
    fun `formatLicense devolve nulo para licenca vazia`() {
        assertThat(formatLicense(null)).isNull()
        assertThat(formatLicense("")).isNull()
        assertThat(formatLicense("  ")).isNull()
    }

    @Test
    fun `so links https com host abrem`() {
        assertThat(isSafeLink("https://xeno-canto.org/403887")).isTrue()
        assertThat(isSafeLink("HTTPS://www.inaturalist.org/observations/1")).isTrue()
    }

    @Test
    fun `javascript http vazio e https sem host nao abrem`() {
        listOf(null, "", "http://www.inaturalist.org/observations/1", "javascript:alert(1)", "https://", "ftp://x.org", "//x.org").forEach {
            assertThat(isSafeLink(it)).isFalse()
        }
    }
}
