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

import io.reactivex.Observable
import io.reactivex.Single
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import tornadofx.*
import java.net.InetAddress

/**
 * HTTP endpoints for radio-browser.info API
 */
interface StationsApi {

    @POST("json/stations/bycountry/{countryCode}")
    fun getStationsByCountry(@Body countriesBody: CountriesBody, @Path("countryCode") countryCode: String): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Single<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Observable<List<Station>>

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

        private const val defaultApiServer = "de1.api.radio-browser.info"
        private const val defaultDnsHost = "all.api.radio-browser.info"

        //try to connect to working API server
        private val inetAddressHostname: String by lazy {
            try {
                InetAddress.getAllByName(defaultDnsHost)[0].canonicalHostName
            } catch (e: Exception) {
                //fallback
                app.config.string(Config.Keys.apiServer, defaultApiServer)
            }
        }

        //API server URL property which is actually used for requests
        //Can be changed in app: About -> server selection
        val hostname: String = ""
            get() = when {
                app.config.string(Config.Keys.apiServer) != null -> app.config.string(Config.Keys.apiServer)!!
                field.isEmpty() -> inetAddressHostname
                else -> field
            }

        val client by lazy { ApiClient("https://$hostname").create(StationsApi::class) }
    }
}