package com.velosobr.passarim.feature.detail.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.velosobr.passarim.core.domain.Credit
import com.velosobr.passarim.core.domain.SpeciesSummary
import kotlin.test.Test

class SpeciesDetailTest {
    private val credit = Credit("autor", "CC BY", "inaturalist", null)

    private fun photo(
        thumb: String? = "t",
        medium: String? = "m",
        large: String? = "l",
    ) = Photo(thumb, medium, large, credit)

    private val detail =
        SpeciesDetail(
            id = "pitangus-sulphuratus",
            commonName = "Bem-te-vi",
            scientificName = "Pitangus sulphuratus",
            family = "Tyrannidae",
            conservation = null,
            sizeCm = null,
            diet = null,
            description = "",
            descriptionCredit = credit,
            facts = emptyList(),
            biomes = emptyList(),
            photos = listOf(photo(thumb = "thumb-1"), photo(thumb = "thumb-2")),
            audio = null,
            whereToFind = WhereToFind(emptyList(), emptyList(), emptyList()),
        )

    @Test
    fun `o hero usa a foto grande e cai para a media`() {
        assertThat(photo().heroUrl).isEqualTo("l")
        assertThat(photo(large = null).heroUrl).isEqualTo("m")
        assertThat(photo(medium = null, large = null).heroUrl).isNull()
    }

    @Test
    fun `a galeria usa a foto media e cai para a grande`() {
        assertThat(photo().galleryUrl).isEqualTo("m")
        assertThat(photo(medium = null).galleryUrl).isEqualTo("l")
        assertThat(photo(medium = null, large = null).galleryUrl).isNull()
    }

    @Test
    fun `o resumo do favorito usa a miniatura da primeira foto`() {
        assertThat(detail.toSummary()).isEqualTo(SpeciesSummary("pitangus-sulphuratus", "Bem-te-vi", "Pitangus sulphuratus", "thumb-1"))
        assertThat(detail.copy(photos = emptyList()).toSummary().thumbnailUrl).isNull()
    }
}
