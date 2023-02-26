package online.hudacek.fxradio.apiclient.musicbrainz.model

data class SearchResult(
    val created: String,
    val count: Int,
    val offset: Long,
    val releases: List<Release>
)
