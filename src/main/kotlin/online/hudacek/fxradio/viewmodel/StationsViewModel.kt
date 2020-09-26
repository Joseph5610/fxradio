package online.hudacek.fxradio.viewmodel

import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.RefreshFavourites
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

class StationsModel {
    val stations: ObservableList<Station> by property(observableListOf(Station.stub()))
    val stationsViewState = objectProperty(StationsViewState.Normal)
}

enum class StationsViewState {
    Normal, Error, Loading, NoResults, ShortQuery
}

/**
 * Stations view model
 * -------------------
 * Holds information about currently shown
 * stations inside [online.hudacek.fxradio.views.StationsDataGridView]
 */
class StationsViewModel : ItemViewModel<StationsModel>() {

    private val logger = KotlinLogging.logger {}

    private val stationsHistoryView: StationsHistoryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    val stationsProperty = bind(StationsModel::stations) as ListProperty
    val stationsViewStateProperty = bind(StationsModel::stationsViewState) as ObjectProperty

    init {
        libraryViewModel.selectedProperty.onChange {
            if (it != null) {
                showLibraryType(it.type, it.params)
            }
        }

        subscribe<RefreshFavourites> {
            libraryViewModel.selectedProperty.value.let { value ->
                if (value.type == LibraryType.Favourites) {
                    showLibraryType(value.type, value.params)
                }
            }
        }
    }

    //retrieve top voted stations list from endpoint
    private val topStations: Disposable
        get() = StationsApi.service
                .getTopStations()
                .compose(applySchedulers())
                .subscribe(::handleResponse, ::handleError)

    //retrieve favourites from DB
    private val favourites: Disposable
        get() = Station.favourites()
                .compose(applySchedulers())
                .subscribe(::handleResponse, ::handleError)

    //retrieve history list
    private val history
        get() = handleResponse(stationsHistoryView.stationsProperty)

    //retrieve all stations from given country from endpoint
    private fun stationsByCountry(country: String): Disposable = StationsApi.service
            .getStationsByCountry(CountriesBody(), country)
            .compose(applySchedulers())
            .subscribe(::handleResponse, ::handleError)

    //search for station name on endpoint
    private fun search(name: String) {
        if (name.length > 2) {
            StationsApi.service
                    .searchStationByName(SearchBody(name))
                    .compose(applySchedulers())
                    .subscribe(::handleResponse, ::handleError)
        } else {
            stationsViewStateProperty.value = StationsViewState.ShortQuery
        }
    }

    private fun handleResponse(stations: List<Station>) {
        stationsProperty.set(stations.asObservable())
        if (stations.isEmpty()) {
            stationsViewStateProperty.value = StationsViewState.NoResults
        } else {
            stationsViewStateProperty.value = StationsViewState.Normal
        }
    }

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        stationsViewStateProperty.value = StationsViewState.Error
    }

    private fun showLibraryType(libraryType: LibraryType, params: String) {
        stationsViewStateProperty.value = StationsViewState.Loading
        when (libraryType) {
            LibraryType.Country -> stationsByCountry(params)
            LibraryType.Favourites -> favourites
            LibraryType.History -> history
            LibraryType.Search -> search(params)
            else -> topStations
        }
    }

    override fun toString() = "StationsViewModel(stationsProperty=${stationsProperty.value})"
}

