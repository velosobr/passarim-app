package com.velosobr.passarim.core.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.core.domain.AppConfig
import kotlin.test.Test

class MediaUrlRewriterTest {
    private fun rewriter(override: String?) =
        MediaUrlRewriter(
            AppConfig(isDebug = override != null, baseUrl = "http://10.0.2.2:8080", mediaHostOverride = override, versionName = "1.0.0"),
        )

    @Test
    fun `troca so o host de localhost e preserva porta caminho e query`() {
        val out = rewriter("10.0.2.2").rewrite("http://localhost:8888/buckets/passarim-media/a.jpg?v=2")
        assertThat(out).isEqualTo("http://10.0.2.2:8888/buckets/passarim-media/a.jpg?v=2")
    }

    @Test
    fun `127_0_0_1 tambem e tratado como local`() {
        val out = rewriter("192.168.0.10").rewrite("http://127.0.0.1:8888/x.aac")
        assertThat(out).isEqualTo("http://192.168.0.10:8888/x.aac")
    }

    @Test
    fun `sem override a URL nao muda`() {
        val url = "http://localhost:8888/a.jpg"
        assertThat(rewriter(null).rewrite(url)).isEqualTo(url)
    }

    @Test
    fun `host que nao e local nunca e reescrito`() {
        val url = "https://cdn.passarim.app/a.jpg"
        assertThat(rewriter("10.0.2.2").rewrite(url)).isEqualTo(url)
    }

    @Test
    fun `URL invalida volta como veio`() {
        assertThat(rewriter("10.0.2.2").rewrite("não é url")).isEqualTo("não é url")
    }

    @Test
    fun `nulo continua nulo`() {
        assertThat(rewriter("10.0.2.2").rewrite(null)).isEqualTo(null)
    }
}
