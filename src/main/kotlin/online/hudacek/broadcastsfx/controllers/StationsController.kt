package online.hudacek.broadcastsfx.controllers

import io.reactivex.Observable
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.model.Station
import tornadofx.Controller

class StationsController : Controller() {

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    fun getStationsByCountry(country: String): Observable<List<Station>> {
        return if (country == "") {
            stationsApi.getTopStations()
        } else {
            stationsApi.getStationsByCountry(country)
        }
    }

    fun getTopStations() = stationsApi.getTopStations()
}