package online.hudacek.fxradio.apiclient.musicbrainz.model

data class SearchResult(
    val created: String,
    val count: Long,
    val offset: Long,
    val releases: List<Release>
)
