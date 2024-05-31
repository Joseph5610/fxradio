package online.hudacek.fxradio.apiclient.musicbrainz.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchResult(
    val created: String,
    val count: Int,
    val offset: Long,
    val releases: List<Release>
)
