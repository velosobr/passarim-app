// A jornada do app: Passarim (adulto) ou Passarim Kids. O tema escolhe a
// variante do PiuDS por ela, e a escolha fica gravada nas preferências
// (Settings.journey, no Settings.kt).

package com.velosobr.passarim.core.domain

/** ADULT é o Passarim de sempre; KIDS é a jornada das crianças de 8 a 12 anos. */
enum class Journey { ADULT, KIDS }
