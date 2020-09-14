package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.LibraryTypeChanged
import online.hudacek.fxradio.events.LibraryTypeChangedConditional
import tornadofx.*

class StationsModel {
    val stations: ObservableList<Station> by property(observableListOf(Station.stub()))
}

enum class StationsViewState {
    Normal, Error, Loading, NoResults, ShortQuery
}

class StationsViewModel : ItemViewModel<StationsModel>() {

    private val logger = KotlinLogging.logger {}

    private val stationsHistoryView: StationsHistoryViewModel by inject()

    val stationsProperty = bind(StationsModel::stations) as ListProperty
    val stationViewStatus = objectProperty(StationsViewState.Normal)

    val currentLibType = BehaviorSubject.create<LibraryType>()
    val currentLibParams = BehaviorSubject.create<String>()

    init {
        currentLibType.startWith(LibraryType.TopStations)
        currentLibParams.startWith("")

        //Handle change of stations library
        subscribe<LibraryTypeChanged> {
            currentLibParams.onNext(it.params)
            currentLibType.onNext(it.type)
            showLibraryType(it.type, it.params)
        }

        subscribe<LibraryTypeChangedConditional> {
            currentLibType.value?.let { value ->
                if (it.onlyWhen == value) {
                    showLibraryType(value, currentLibParams.value ?: "")
                }
            }
        }
    }

    //retrieve favourites from DB
    private fun getFavourites(): Disposable = Station.favourites()
            .observeOnFx()
            .subscribeOn(Schedulers.io())
            .subscribe(::handleResponse, ::handleError)

    //retrieve all stations from given country from endpoint
    private fun getStationsByCountry(country: String): Disposable = StationsApi.service
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe(::handleResponse, ::handleError)

    //search for station name on endpoint
    private fun searchStations(name: String) {
        if (name.length > 2) {
            StationsApi.service
                    .searchStationByName(SearchBody(name))
                    .subscribeOn(Schedulers.io())
                    .observeOnFx()
                    .subscribe(::handleResponse, ::handleError)
        } else {
            stationViewStatus.value = StationsViewState.ShortQuery
        }
    }

    //retrieve history list
    private fun getHistory() = handleResponse(stationsHistoryView.stations)

    //retrieve top voted stations list from endpoint
    private fun getTopStations(): Disposable = StationsApi.service
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe(::handleResponse, ::handleError)

    private fun handleResponse(stations: List<Station>) {
        stationsProperty.set(stations.asObservable())
        if (stations.isEmpty()) {
            stationViewStatus.value = StationsViewState.NoResults
        } else {
            stationViewStatus.value = StationsViewState.Normal
        }
    }

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        stationViewStatus.value = StationsViewState.Error
    }

    private fun showLibraryType(libraryType: LibraryType, params: String) {
        stationViewStatus.value = StationsViewState.Loading
        when (libraryType) {
            LibraryType.Country -> getStationsByCountry(params)
            LibraryType.Favourites -> getFavourites()
            LibraryType.History -> getHistory()
            LibraryType.Search -> searchStations(params)
            else -> getTopStations()
        }
    }

    override fun toString() = "StationsViewModel(stationsProperty=${stationsProperty.value})"
}

