package com.velosobr.passarim.feature.detail.domain

private val SPECIES_ID = Regex("^[a-z0-9-]{1,80}$")

fun isValidSpeciesId(id: String): Boolean = SPECIES_ID.matches(id)
