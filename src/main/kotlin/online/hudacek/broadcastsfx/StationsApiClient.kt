package online.hudacek.broadcastsfx

import io.reactivex.Observable
import online.hudacek.broadcastsfx.model.Countries
import online.hudacek.broadcastsfx.model.SearchModel
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.model.Stats
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import tornadofx.controlsfx.errorNotification
import tornadofx.fail
import tornadofx.runAsync
import tornadofx.success
import java.lang.RuntimeException
import java.net.InetAddress
import java.net.UnknownHostException

interface StationsApiClient {

    @GET("json/stations/bycountry/{countryCode}")
    fun getStationsByCountry(@Path("countryCode") countryCode: String): Observable<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchModel: SearchModel): Observable<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Observable<List<Station>>

    @GET("json/countries")
    fun getCountries(): Observable<List<Countries>>

    @GET("json/stats")
    fun getStats(): Observable<Stats>

    companion object {

        private val inetAddressHostname: String by lazy {
            try {
                InetAddress.getAllByName("all.api.radio-browser.info")[0].canonicalHostName
            } catch (e: Exception) {
                //fallback
                //tornadofx.error("Can't connect to server", "We are unable to connect to API server. Are you sure you are connected to internet?")
                "de1.api.radio-browser.info"
            }
        }

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