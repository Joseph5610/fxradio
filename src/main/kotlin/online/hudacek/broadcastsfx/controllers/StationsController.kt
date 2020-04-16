package online.hudacek.broadcastsfx.controllers

import io.reactivex.Observable
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.data.Station
import online.hudacek.broadcastsfx.events.StationChangedEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import tornadofx.Controller

class StationsController : Controller() {

    private val stationsApi by lazy { StationsApiClient.create() }

    fun getStationsByCountry(country: String): Observable<List<Station>> {
        return if (country == "") {
            stationsApi.getTopStations()
        } else {
            stationsApi.getStationsByCountry(country)
        }
    }

    fun getTopStations() = stationsApi.getTopStations()

    fun playStream(station: Station) {
        fire(StationChangedEvent(station, PlayingStatus.Playing))
    }
}