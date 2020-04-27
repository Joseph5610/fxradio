package online.hudacek.broadcastsfx.controllers

import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.SearchFieldChanged
import online.hudacek.broadcastsfx.events.StationListReloadEvent
import online.hudacek.broadcastsfx.events.StationListType
import online.hudacek.broadcastsfx.model.Countries
import online.hudacek.broadcastsfx.model.Station
import tornadofx.Controller
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.toObservable

class LeftPaneController : Controller() {

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    val libraryItems by lazy {
        observableListOf(StationListType.TopStations.name)
    }

    fun searchStation(searchString: String) = fire(SearchFieldChanged(searchString))

    fun getCountries(): Observable<List<Countries>> = stationsApi
            .getCountries()
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())

    fun loadStationsByCountry(country: String) = fire(StationListReloadEvent(StationListType.Country, country))

    fun loadTopListOfStations() = fire(StationListReloadEvent(StationListType.TopStations))
}