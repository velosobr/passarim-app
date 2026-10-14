package com.velosobr.passarim.feature.detail.domain

import com.velosobr.passarim.core.domain.DataError
import com.velosobr.passarim.core.domain.Result

interface DetailRepository {
    suspend fun get(id: String): Result<SpeciesDetail, DataError.Network>
}
