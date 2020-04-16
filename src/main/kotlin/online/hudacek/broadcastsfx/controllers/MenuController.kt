package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.StationDirectoryReloadEvent
import tornadofx.Controller

class MenuController : Controller() {

    private val stations by lazy { StationsApiClient.create() }

    fun getCountries() = stations.getCountries()

    fun reloadStations(country : String) {
        fire(StationDirectoryReloadEvent(country))
    }
}