package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.StationListReloadEvent
import online.hudacek.broadcastsfx.events.StationDirectoryType
import tornadofx.Controller

class MenuController : Controller() {

    private val stations by lazy { StationsApiClient.client }

    fun getCountries() = stations.getCountries()

    fun loadStationsByCountry(country: String) = fire(StationListReloadEvent(country, StationDirectoryType.Country))

    fun loadTopListOfStations() = fire(StationListReloadEvent("", StationDirectoryType.TopList))
}