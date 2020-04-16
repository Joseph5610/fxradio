package online.hudacek.broadcastsfx

import io.reactivex.Observable
import online.hudacek.broadcastsfx.data.Countries
import online.hudacek.broadcastsfx.data.Station
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

    companion object {
        private val hostname: InetAddress by lazy { InetAddress.getAllByName("all.api.radio-browser.info")[0] }

        val client: StationsApiClient by lazy {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://" + hostname.canonicalHostName)
                    .build()

            retrofit.create(StationsApiClient::class.java)
        }
    }
}