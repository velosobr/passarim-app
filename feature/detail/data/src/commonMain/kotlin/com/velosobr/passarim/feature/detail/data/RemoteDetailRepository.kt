// O Detalhe vindo do BFF.

package com.velosobr.passarim.feature.detail.data

import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.data.safeCall
import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result
import com.velosobr.passarim.core.domain.map
import com.velosobr.passarim.feature.detail.domain.DetailRepository
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.encodeURLPathPart

/**
 * `GET /v1/species/{id}`. encodeURLPathPart escapa o id para ele não mudar o
 * caminho da URL (um "/" no id viraria outro trecho do caminho).
 */
class RemoteDetailRepository(
    private val client: HttpClient,
    private val rewriter: MediaUrlRewriter,
) : DetailRepository {
    override suspend fun get(id: String): Result<SpeciesDetail, DataError.Network> =
        safeCall<SpeciesDetailDto> { client.get("/v1/species/${id.encodeURLPathPart()}") }.map { it.toDomain(rewriter) }
}
