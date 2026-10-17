// Contrato do Detalhe.

package com.velosobr.passarim.feature.detail.domain

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result

/** Busca a espécie completa pelo id. */
interface DetailRepository {
    suspend fun get(id: String): Result<SpeciesDetail, DataError.Network>
}
