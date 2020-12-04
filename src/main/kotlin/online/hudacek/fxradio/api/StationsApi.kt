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

package online.hudacek.fxradio.api

import io.reactivex.Single
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.Property
import online.hudacek.fxradio.api.model.*
import online.hudacek.fxradio.viewmodel.ServersModel
import online.hudacek.fxradio.viewmodel.ServersViewModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import tornadofx.Component

/**
 * Stations API
 * --------------
 * HTTP endpoints for radio-browser.info API
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
    fun add(@Body addBody: AddStationBody): Single<AddStationResult>

    @POST("json/countries")
    fun getCountries(@Body countriesBody: CountriesBody): Single<List<Countries>>

    @GET("json/stats")
    fun getStats(): Single<Stats>

    @GET("json/vote/{uuid}")
    fun vote(@Path("uuid") uuid: String): Single<VoteResult>

    @GET("json/url/{uuid}")
    fun click(@Path("uuid") uuid: String): Single<ClickResult>

    companion object : Component() {

        private val logger = KotlinLogging.logger {}

        private val viewModel: ServersViewModel by inject()

        private val savedServerValue = Property(Properties.API_SERVER)

        init {
            //The little logic here: Try to init model with the previously stored value
            //Only if the value is not stored try to get it by calling InetAddress.getAllByName
            if (savedServerValue.isPresent) {
                logger.debug { "Setting model from saved state" }
                viewModel.item = ServersModel(savedServerValue.get())
            } else {
                viewModel.loadAllServers()

                viewModel.serversProperty.let {
                    if (it.isNotEmpty()) {
                        logger.debug { "Setting model from available servers" }
                        viewModel.item = ServersModel(savedServerValue.get(it[0]), it)
                    } else {
                        logger.debug { "Setting fallback default value of model" }
                        viewModel.item = ServersModel(Config.Resources.defaultApiServer)
                    }
                    //Save the value for later use
                    savedServerValue.save(viewModel.selectedProperty.value)

                }
            }
        }

        val client by lazy { ApiClient("https://${viewModel.selectedProperty.value}") }
        val service by lazy { client.create(StationsApi::class) }
    }
}