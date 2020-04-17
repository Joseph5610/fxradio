package online.hudacek.broadcastsfx.controllers

import io.reactivex.Observable
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.CurrentStation
import online.hudacek.broadcastsfx.model.StationViewModel
import tornadofx.Controller

class StationsController : Controller() {

    private val stationsApi by lazy { StationsApiClient.client }
    val currentStation: StationViewModel by inject()

    fun getStationsByCountry(country: String): Observable<List<Station>> {
        return if (country == "") {
            stationsApi.getTopStations()
        } else {
            stationsApi.getStationsByCountry(country)
        }
    }

    fun getTopStations() = stationsApi.getTopStations()

    fun playStream(station: Station) {
        currentStation.item = CurrentStation(station)
        fire(PlaybackChangeEvent(PlayingStatus.Playing))
    }
}