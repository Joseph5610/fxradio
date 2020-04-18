package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.StationListReloadEvent
import online.hudacek.broadcastsfx.events.StationDirectoryType
import tornadofx.Controller

class MenuController : Controller() {

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    fun getCountries() = stationsApi.getCountries()

    fun loadStationsByCountry(country: String) = fire(StationListReloadEvent(country, StationDirectoryType.Country))

    fun loadTopListOfStations() = fire(StationListReloadEvent("", StationDirectoryType.TopList))
}