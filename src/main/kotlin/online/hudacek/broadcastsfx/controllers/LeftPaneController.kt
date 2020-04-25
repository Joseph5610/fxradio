package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.SearchFieldChanged
import online.hudacek.broadcastsfx.events.StationListReloadEvent
import online.hudacek.broadcastsfx.events.StationListType
import tornadofx.Controller

class LeftPaneController : Controller() {

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    fun searchStation(searchString: String) = fire(SearchFieldChanged(searchString))

    fun searchFocusChanged() = fire(SearchFieldChanged(""))

    fun getCountries() = stationsApi.getCountries()

    fun loadStationsByCountry(country: String) = fire(StationListReloadEvent(StationListType.Country, country))

    fun loadTopListOfStations() = fire(StationListReloadEvent(StationListType.TopStations))
}