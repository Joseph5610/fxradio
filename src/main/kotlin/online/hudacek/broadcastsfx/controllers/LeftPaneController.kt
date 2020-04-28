package online.hudacek.broadcastsfx.controllers

import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.events.LibrarySearchChanged
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.rest.Countries
import online.hudacek.broadcastsfx.model.rest.HideBrokenBody
import online.hudacek.broadcastsfx.model.Library
import tornadofx.Controller
import tornadofx.observableListOf

class LeftPaneController : Controller() {

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    val libraryItems by lazy {
        observableListOf(
                Library("Top stations", LibraryType.TopStations)
                //Library("History", LibraryType.History)
        )
    }

    fun searchStation(searchString: String) = fire(LibrarySearchChanged(searchString))

    fun getCountries(): Observable<List<Countries>> = stationsApi
            .getCountries(HideBrokenBody())
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())

    fun loadStationsByCountry(country: String) = fire(LibraryRefreshEvent(LibraryType.Country, country))

    fun loadLibrary(libraryType: LibraryType) = fire(LibraryRefreshEvent(libraryType))
}