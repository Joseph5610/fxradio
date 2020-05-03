package online.hudacek.broadcastsfx.controllers

import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.events.LibrarySearchChanged
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.Library
import online.hudacek.broadcastsfx.views.LibraryView
import tornadofx.Controller
import tornadofx.observableListOf

class LibraryController : Controller() {

    private val libraryView: LibraryView by inject()

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    val libraryItems by lazy {
        observableListOf(
                Library("Top stations", LibraryType.TopStations)
                //Library("History", LibraryType.History)
        )
    }

    init {
        getCountries()
    }

    fun searchStation(searchString: String) = fire(LibrarySearchChanged(searchString))

    fun getCountries(): Disposable = stationsApi
            .getCountries(CountriesBody())
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
            .subscribe(
                    {
                        libraryView.showCountries(it)
                    },
                    {
                        libraryView.showError()
                    }
            )

    fun loadStationsByCountry(country: String) = fire(LibraryRefreshEvent(LibraryType.Country, country))

    fun loadLibrary(libraryType: LibraryType) = fire(LibraryRefreshEvent(libraryType))
}