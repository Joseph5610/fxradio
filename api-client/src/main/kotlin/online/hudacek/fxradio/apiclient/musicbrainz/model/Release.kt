package online.hudacek.fxradio.apiclient.musicbrainz.model

import com.google.gson.annotations.SerializedName

data class Release(
    val id: String,
    val score: Int,
    val title: String,
    val status: String,
    @SerializedName("artist-credit") val artistCredit: List<ArtistCredit>
)