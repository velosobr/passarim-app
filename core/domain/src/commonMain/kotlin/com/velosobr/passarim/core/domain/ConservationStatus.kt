package com.velosobr.passarim.core.domain

enum class ConservationStatus { LC, NT, VU, EN, CR, EW, EX, DD }

fun conservationStatusOf(code: String?): ConservationStatus? = ConservationStatus.entries.firstOrNull { it.name == code }
