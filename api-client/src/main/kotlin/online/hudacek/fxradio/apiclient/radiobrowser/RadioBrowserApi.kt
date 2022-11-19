/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.apiclient.radiobrowser

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.ApiDefinition
import online.hudacek.fxradio.apiclient.radiobrowser.model.NewStationResponse
import online.hudacek.fxradio.apiclient.radiobrowser.model.AllStationsRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.ClickResponse
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchByTagRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchByUUIDsRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.apiclient.radiobrowser.model.NewStationRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.StatsResponse
import online.hudacek.fxradio.apiclient.radiobrowser.model.VoteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Provides HTTP endpoints for radio-browser.info API
 */
interface RadioBrowserApi : ApiDefinition {

    @POST("json/stations/bycountrycodeexact/{countryCode}")
    fun getStationsByCountryCode(
        @Path("countryCode") countryCode: String,
        @Query("hidebroken") hideBroken: Boolean = true
    ): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchRequest: SearchRequest): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByTag(@Body searchByTagRequest: SearchByTagRequest): Single<List<Station>>

    @POST("json/stations/byuuid")
    fun searchStationByUUIDs(@Body searchByUUIDsRequest: SearchByUUIDsRequest): Single<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopVotedStations(@Query("hidebroken") hideBroken: Boolean = true): Single<List<Station>>

    @POST("json/stations")
    fun getAllStations(@Body allStationsRequest: AllStationsRequest): Single<List<Station>>

    @POST("json/add")
    fun addStation(@Body newStationRequest: NewStationRequest): Single<NewStationResponse>

    @GET("json/countries")
    fun getCountries(@Query("hidebroken") hideBroken: Boolean = true): Single<List<Country>>

    @GET("json/stats")
    fun getStats(): Single<StatsResponse>

    @GET("json/vote/{uuid}")
    fun addVote(@Path("uuid") uuid: String): Single<VoteResponse>

    @GET("json/url/{uuid}")
    fun click(@Path("uuid") uuid: String): Single<ClickResponse>
}
