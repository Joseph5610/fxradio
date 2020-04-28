package online.hudacek.broadcastsfx

import io.reactivex.Observable
import online.hudacek.broadcastsfx.model.rest.Countries
import online.hudacek.broadcastsfx.model.rest.HideBrokenBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.model.rest.Stats
import online.hudacek.broadcastsfx.model.rest.Station
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.net.InetAddress

/**
 * HTTP endpoints for radio-browser.info API
 */
interface StationsApiClient {

    @POST("json/stations/bycountry/{countryCode}")
    fun getStationsByCountry(@Body countriesBody: HideBrokenBody, @Path("countryCode") countryCode: String): Observable<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Observable<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Observable<List<Station>>

    @POST("json/countries")
    fun getCountries(@Body countriesBody: HideBrokenBody): Observable<List<Countries>>

    @GET("json/stats")
    fun getStats(): Observable<Stats>

    companion object {

        //try to connect to working API server
        private val inetAddressHostname: String by lazy {
            try {
                InetAddress.getAllByName("all.api.radio-browser.info")[0].canonicalHostName
            } catch (e: Exception) {
                //fallback
                //tornadofx.error("Can't connect to server", "We are unable to connect to API server. Are you sure you are connected to internet?")
                "de1.api.radio-browser.info"
            }
        }

        //API server URL property which is actually used for requests
        //Can be changed in app: About -> server selection
        var hostname: String = ""
            get() {
                return if (field.isEmpty()) inetAddressHostname
                else field
            }

        val client: StationsApiClient
            get() {
                val retrofit = Retrofit.Builder()
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://$hostname")
                        .build()

                return retrofit.create(StationsApiClient::class.java)
            }
    }
}