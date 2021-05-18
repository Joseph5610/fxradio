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

package online.hudacek.fxradio.api.stations

import io.reactivex.Single
import online.hudacek.fxradio.api.ApiServiceProvider
import online.hudacek.fxradio.api.stations.model.*
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.viewmodel.Servers
import online.hudacek.fxradio.viewmodel.ServersViewModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import tornadofx.Component

/**
 * Provides HTTP endpoints for radio-browser.info API
 */
interface StationsApi {

    @POST("json/stations/bycountryexact/{countryName}")
    fun getStationsByCountry(@Body countriesBody: CountriesBody, @Path("countryName") countryName: String): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByTag(@Body searchBody: SearchByTagBody): Single<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Single<List<Station>>

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

    companion object : Component() {

        private val viewModel: ServersViewModel by inject()

        private val apiServerProperty = Property(Properties.ApiServer)

        val serviceProvider: ApiServiceProvider by lazy {
            if (apiServerProperty.isPresent) {
                viewModel.item = Servers(apiServerProperty.get())
            }
            ApiServiceProvider("https://${viewModel.selectedProperty.value}")
        }

        val service by lazy { serviceProvider.get<StationsApi>() }
    }
}