package online.hudacek.fxradio.apiclient.musicbrainz

import io.reactivex.rxjava3.core.Single
import online.hudacek.fxradio.apiclient.ApiDefinition
import online.hudacek.fxradio.apiclient.musicbrainz.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

private const val DEFAULT_FORMAT = "json"

/**
 * musicbrainz.org API service
 */
interface MusicBrainzApi : ApiDefinition {

    @GET("release")
    fun getReleases(@Query("query") query: String, @Query("fmt") fmt: String = DEFAULT_FORMAT): Single<SearchResult>
}