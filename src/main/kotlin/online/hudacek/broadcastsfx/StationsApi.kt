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

package online.hudacek.broadcastsfx

import io.reactivex.Observable
import okhttp3.OkHttpClient
import online.hudacek.broadcastsfx.model.rest.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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
    fun getStationsByCountry(@Body countriesBody: CountriesBody, @Path("countryCode") countryCode: String): Observable<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Observable<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Observable<List<Station>>

    @GET("json/stations/byuuid/{uuid}")
    fun getStationInfo(@Path("uuid") uuid: String): Observable<List<Station>>

    @POST("json/add")
    fun add(@Body addBody: AddStationBody): Observable<AddStationResult>

    @GET("json/tags")
    fun getTags(): Observable<List<Tags>>

    @POST("json/countries")
    fun getCountries(@Body countriesBody: CountriesBody): Observable<List<Countries>>

    @GET("json/stats")
    fun getStats(): Observable<Stats>

    @GET("json/vote/{uuid}")
    fun vote(@Path("uuid") uuid: String): Observable<VoteResult>

    companion object : Component() {

        //What is app sending as a User Agent string
        private val userAgentIdentifier = "${FxRadio.appName}/${FxRadio.version}"

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
        var hostname: String = ""
            get() = when {
                app.config.string(Config.Keys.apiServer) != null -> app.config.string(Config.Keys.apiServer)!!
                field.isEmpty() -> inetAddressHostname
                else -> field
            }

        //Construct http client with custom user agent
        private val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                            chain.request()
                                    .newBuilder()
                                    .header("User-Agent", userAgentIdentifier)
                                    .build()
                    )
                }
                .build()

        val client: StationsApi
            get() = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://$hostname")
                    .client(httpClient)
                    .build()
                    .create(StationsApi::class.java)
    }
}