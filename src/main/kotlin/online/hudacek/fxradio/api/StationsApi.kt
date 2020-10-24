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
import online.hudacek.fxradio.Property
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.api.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import tornadofx.*
import java.net.InetAddress

/**
 * Stations API
 * --------------
 * HTTP endpoints for radio-browser.info API
 */
interface StationsApi {

    @POST("json/stations/bycountry/{countryCode}")
    fun getStationsByCountry(@Body countriesBody: CountriesBody, @Path("countryCode") countryCode: String): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Single<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Single<List<Station>>

    @GET("json/stations/byuuid/{uuid}")
    fun getStationInfo(@Path("uuid") uuid: String): Single<List<Station>>

    @POST("json/add")
    fun add(@Body addBody: AddStationBody): Single<AddStationResult>

    @GET("json/tags")
    fun getTags(): Single<List<Tags>>

    @POST("json/countries")
    fun getCountries(@Body countriesBody: CountriesBody): Single<List<Countries>>

    @GET("json/stats")
    fun getStats(): Single<Stats>

    @GET("json/vote/{uuid}")
    fun vote(@Path("uuid") uuid: String): Single<VoteResult>

    companion object : Component() {
        private val logger = KotlinLogging.logger {}

        private const val defaultApiServer = "de1.api.radio-browser.info"
        private const val defaultDnsHost = "all.api.radio-browser.info"

        //try to find working API server
        private val inetAddressHostname: String by lazy {
            try {
                logger.debug { "Getting hostname from DNS..." }
                val hostname = InetAddress.getAllByName(defaultDnsHost)[0].canonicalHostName
                //Save the hostname for future
                Property(Properties.API_SERVER).save(hostname)
                hostname
            } catch (e: Exception) {
                logger.error(e) { "Hostname resolving failed" }

                //use stored value or hardcoded value as fallback
                Property(Properties.API_SERVER).get(defaultApiServer)
            }
        }

        private val appConfig = Property(Properties.API_SERVER)

        //API server URL property which is used for requests
        //If nothing is stored in app.properties, it will try to find working API server from inetAddressHostname property
        //Otherwise stored value is uses
        val hostname: String = ""
            get() = when {
                appConfig.isPresent -> appConfig.get()
                field.isEmpty() -> inetAddressHostname
                else -> field
            }

        val client by lazy { ApiClient("https://$hostname") }
        val service by lazy { client.create(StationsApi::class) }
    }
}