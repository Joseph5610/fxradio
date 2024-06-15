package online.hudacek.fxradio.apiclient.musicbrainz.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Release(
    val id: String,
    val score: Int,
    val title: String,
    val status: String?,
    @JsonProperty("artist-credit") val artistCredit: List<ArtistCredit>
)