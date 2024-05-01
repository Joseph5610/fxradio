package online.hudacek.fxradio.apiclient.musicbrainz.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Artist(
    val id: String,
    val name: String,
    val disambiguation: String?
)
