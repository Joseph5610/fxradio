/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.apiclient.radiobrowser

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.ApiDefinition
import online.hudacek.fxradio.apiclient.radiobrowser.model.AddedStation
import online.hudacek.fxradio.apiclient.radiobrowser.model.AllStationsBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.ClickResult
import online.hudacek.fxradio.apiclient.radiobrowser.model.CountriesBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.SearchByTagBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.apiclient.radiobrowser.model.StationBody
import online.hudacek.fxradio.apiclient.radiobrowser.model.StatsResult
import online.hudacek.fxradio.apiclient.radiobrowser.model.VoteResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Provides HTTP endpoints for radio-browser.info API
 */
interface RadioBrowserApi : ApiDefinition {

    @POST("json/stations/bycountryexact/{countryName}")
    fun getStationsByCountry(@Body countriesBody: CountriesBody,
                             @Path("countryName") countryName: String): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByName(@Body searchBody: SearchBody): Single<List<Station>>

    @POST("json/stations/search")
    fun searchStationByTag(@Body searchBody: SearchByTagBody): Single<List<Station>>

    @GET("json/stations/topvote/50")
    fun getTopVotedStations(@Query("hidebroken") hidebroken: Boolean = true): Single<List<Station>>

    @POST("json/stations")
    fun getAllStations(@Body stationsBody: AllStationsBody): Single<List<Station>>

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
}
