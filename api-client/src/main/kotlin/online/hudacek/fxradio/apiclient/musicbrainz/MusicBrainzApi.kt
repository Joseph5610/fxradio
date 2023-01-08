package online.hudacek.fxradio.apiclient.musicbrainz

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.ApiDefinition
import online.hudacek.fxradio.apiclient.musicbrainz.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicBrainzApi : ApiDefinition {

    @GET("release")
    fun search(@Query("query") query: String, @Query("fmt") fmt: String = "json"): Single<SearchResult>
}