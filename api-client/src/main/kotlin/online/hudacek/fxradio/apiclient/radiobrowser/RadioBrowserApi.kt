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

import io.reactivex.rxjava3.core.Single
import online.hudacek.fxradio.apiclient.ApiDefinition
import online.hudacek.fxradio.apiclient.radiobrowser.model.AdvancedSearchRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.ClickResponse
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.AddStationRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.AddStationResponse
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.apiclient.radiobrowser.model.StatsResponse
import online.hudacek.fxradio.apiclient.radiobrowser.model.Tag
import online.hudacek.fxradio.apiclient.radiobrowser.model.VoteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private const val DEFAULT_HIDE_BROKEN = true

/**
 * radio-browser.info API service
 */
interface RadioBrowserApi : ApiDefinition {

    @GET("json/stations/topvote/150")
    fun getTopVotedStations(@Query("hidebroken") hideBroken: Boolean = DEFAULT_HIDE_BROKEN): Single<List<Station>>

    @GET("json/stations/bycountrycodeexact/{countryCode}")
    fun getStationsByCountryCode(
        @Path("countryCode") countryCode: String,
        @Query("hidebroken") hideBroken: Boolean = DEFAULT_HIDE_BROKEN
    ): Single<List<Station>>

    @GET("json/stations/byuuid/{uuid}")
    fun getStationsByUUID(
        @Path("uuid") uuid: String,
        @Query("hidebroken") hideBroken: Boolean = DEFAULT_HIDE_BROKEN
    ): Single<List<Station>>

    @GET("json/countries")
    fun getCountries(@Query("hidebroken") hideBroken: Boolean = DEFAULT_HIDE_BROKEN): Single<List<Country>>

    @GET("json/tags")
    fun getTags(
        @Query("order") order: String,
        @Query("limit") limit: Int,
        @Query("reverse") reverse: Boolean
    ): Single<List<Tag>>

    @GET("json/vote/{uuid}")
    fun addVote(@Path("uuid") uuid: String): Single<VoteResponse>

    @GET("json/url/{uuid}")
    fun addClick(@Path("uuid") uuid: String): Single<ClickResponse>

    @GET("json/stats")
    fun getStats(): Single<StatsResponse>

    @POST("json/stations/search")
    fun advancedSearch(@Body advancedSearchRequest: AdvancedSearchRequest): Single<List<Station>>

    @POST("json/add")
    fun addStation(@Body addStationRequest: AddStationRequest): Single<AddStationResponse>
}
