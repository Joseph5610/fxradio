package online.hudacek.fxradio.apiclient.musicbrainz.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ArtistCredit(
    val name: String,
    val artist: Artist
)
