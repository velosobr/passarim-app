// Conversão dos DTOs do Detalhe no domínio: textos vazios viram null, as
// licenças são formatadas e o host das mídias é trocado em debug.

package com.velosobr.passarim.feature.detail.data

import com.velosobr.passarim.core.data.MediaUrlRewriter
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.core.domain.conservationStatusOf
import com.velosobr.passarim.feature.detail.domain.Audio
import com.velosobr.passarim.feature.detail.domain.Biome
import com.velosobr.passarim.feature.detail.domain.Cluster
import com.velosobr.passarim.feature.detail.domain.Photo
import com.velosobr.passarim.feature.detail.domain.SpeciesDetail
import com.velosobr.passarim.feature.detail.domain.WhereToFind
import com.velosobr.passarim.feature.detail.domain.formatLicense

/** Converte a espécie inteira. Fatos vazios são descartados. */
internal fun SpeciesDetailDto.toDomain(rewriter: MediaUrlRewriter) =
    SpeciesDetail(
        id = id,
        commonName = commonName,
        scientificName = scientificName,
        family = family,
        conservation = conservationStatusOf(conservationStatus?.code),
        sizeCm = sizeCm,
        diet = diet.orNullIfBlank(),
        description = description,
        descriptionCredit = descriptionCredit.toDomain(),
        facts = facts.mapNotNull { it.text.orNullIfBlank() },
        biomes = chips.map { Biome(it.code, it.label) },
        photos = photos.map { it.toDomain(rewriter) },
        audio = audio?.toDomain(rewriter),
        whereToFind =
            WhereToFind(
                states = whereToFind.states,
                biomes = whereToFind.biomes,
                clusters = whereToFind.clusters.map { Cluster(it.lat, it.lng, it.count) },
            ),
    )

/** Converte o crédito; a licença "CC-BY-4.0" vira "CC BY 4.0". */
private fun CreditDto.toDomain() =
    Credit(
        author = author.orNullIfBlank(),
        license = formatLicense(license),
        source = source.orNullIfBlank(),
        sourceUrl = sourceUrl.orNullIfBlank(),
    )

/** Converte uma foto, trocando o host das três URLs em debug. */
private fun PhotoDto.toDomain(rewriter: MediaUrlRewriter) =
    Photo(
        thumbUrl = rewriter.rewrite(thumbUrl.orNullIfBlank()),
        mediumUrl = rewriter.rewrite(mediumUrl.orNullIfBlank()),
        largeUrl = rewriter.rewrite(largeUrl.orNullIfBlank()),
        credit = credit.toDomain(),
    )

/** Converte o canto; sem URL válida, não há canto (null). */
private fun AudioDto.toDomain(rewriter: MediaUrlRewriter): Audio? =
    rewriter.rewrite(url.orNullIfBlank())?.let { Audio(it, durationMs, credit.toDomain()) }

/** Texto nulo, vazio ou só com espaços vira null. */
private fun String?.orNullIfBlank(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
