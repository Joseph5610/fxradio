package online.hudacek.broadcastsfx

import io.reactivex.Observable
import online.hudacek.broadcastsfx.data.CountriesModel
import online.hudacek.broadcastsfx.data.TopStationsModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.net.InetAddress

interface StationsApiClient {

    @GET("json/stations/bycountry/{countryCode}")
    fun getStationsByCountry(@Path("countryCode") countryCode: String): Observable<List<TopStationsModel>>

    @GET("json/stations")
    fun getAllStations(): Observable<List<TopStationsModel>>

    @GET("json/stations/topvote/50")
    fun getTopStations(): Observable<List<TopStationsModel>>

    @GET("json/countries")
    fun getCountries(): Observable<List<CountriesModel>>

    companion object {

        fun create(): StationsApiClient {

            val list = InetAddress.getAllByName("all.api.radio-browser.info")
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://${list[0].canonicalHostName}/")
                    .build()

            return retrofit.create(StationsApiClient::class.java)
        }
    }

}