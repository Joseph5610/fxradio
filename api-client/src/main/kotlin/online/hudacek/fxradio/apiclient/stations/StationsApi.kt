/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.apiclient.stations

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.stations.model.AddedStation
import online.hudacek.fxradio.apiclient.stations.model.ClickResult
import online.hudacek.fxradio.apiclient.stations.model.CountriesBody
import online.hudacek.fxradio.apiclient.stations.model.Country
import online.hudacek.fxradio.apiclient.stations.model.SearchBody
import online.hudacek.fxradio.apiclient.stations.model.SearchByTagBody
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.apiclient.stations.model.StationBody
import online.hudacek.fxradio.apiclient.stations.model.StatsResult
import online.hudacek.fxradio.apiclient.stations.model.VoteResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Provides HTTP endpoints for radio-browser.info API
 */
interface StationsApi {

    @POST("json/stations/bycountryexact/{countryName}")
    fun getStationsByCountry(
            @Body countriesBody: CountriesBody,
            @Path("countryName") countryName: String
    ): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByTag(@Body searchBody: SearchByTagBody): Single<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(@Query("hidebroken") hidebroken: Boolean = true): Single<List<Station>>

    @POST("json/add")
    fun addStation(@Body stationBody: StationBody): Single<AddedStation>

    @POST("json/countries")
    fun getCountries(@Body countriesBody: CountriesBody): Single<List<Country>>

    @GET("json/stats")
    fun getStats(): Single<StatsResult>

    @GET("json/vote/{uuid}")
    fun addVote(@Path("uuid") uuid: String): Single<VoteResult>

    @GET("json/url/{uuid}")
    fun click(@Path("uuid") uuid: String): Single<ClickResult>
}