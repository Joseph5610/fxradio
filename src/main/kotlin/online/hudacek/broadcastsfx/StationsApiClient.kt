package online.hudacek.broadcastsfx

import io.reactivex.Observable
import online.hudacek.broadcastsfx.model.Countries
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.model.Stats
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.net.InetAddress

interface StationsApiClient {

    @GET("json/stations/bycountry/{countryCode}")
    fun getStationsByCountry(@Path("countryCode") countryCode: String): Observable<List<Station>>

    @GET("json/stations")
    fun getAllStations(): Observable<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Observable<List<Station>>

    @GET("json/countries")
    fun getCountries(): Observable<List<Countries>>

    @GET("json/stats")
    fun getStats(): Observable<Stats>

    companion object {

        private val inetAddress: InetAddress by lazy { InetAddress.getAllByName("all.api.radio-browser.info")[0] }

        var hostname = inetAddress.canonicalHostName

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