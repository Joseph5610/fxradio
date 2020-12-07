package online.hudacek.fxradio.viewmodel

import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.SearchByTagBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

enum class StationsViewState {
    Normal, Error, Loading, Empty, ShortQuery
}

class StationsModel {
    val stations: ObservableList<Station> by property(observableListOf())
    val viewState: StationsViewState by objectProperty(StationsViewState.Empty)
}

/**
 * Stations view model
 * -------------------
 * Holds information about currently shown
 * stations inside [online.hudacek.fxradio.views.stations.StationsDataGridView]
 */
class StationsViewModel : ItemViewModel<StationsModel>() {

    private val logger = KotlinLogging.logger {}

    val stationsProperty = bind(StationsModel::stations) as ListProperty
    val viewStateProperty = bind(StationsModel::viewState) as ObjectProperty

    //retrieve top voted stations list from endpoint
    val topStations: Disposable
        get() = StationsApi.service
                .getTopStations()
                .compose(applySchedulers())
                .subscribe(::show, ::handleError)

    //retrieve all stations from given country from endpoint
    fun stationsByCountry(country: String): Disposable = StationsApi.service
            .getStationsByCountry(CountriesBody(), country)
            .compose(applySchedulers())
            .subscribe(::show, ::handleError)

    //search for station name on endpoint
    fun search(name: String) {
        if (name.length > 2) {
            StationsApi.service
                    .searchStationByName(SearchBody(name))
                    .compose(applySchedulers())
                    .subscribe(::show, ::handleError)
        } else {
            viewStateProperty.value = StationsViewState.ShortQuery
        }
    }

    fun searchByTag(tag: String) {
        if (tag.length > 2) {
            StationsApi.service
                    .searchStationByTag(SearchByTagBody(tag))
                    .compose(applySchedulers())
                    .subscribe(::show, ::handleError)
        } else {
            viewStateProperty.value = StationsViewState.ShortQuery
        }
    }

    fun show(stations: List<Station>) {
        stationsProperty.set(stations.asObservable())
        if (stations.isEmpty()) {
            viewStateProperty.value = StationsViewState.Empty
        } else {
            viewStateProperty.value = StationsViewState.Normal
        }
    }

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        viewStateProperty.value = StationsViewState.Error
    }

    override fun toString() = "StationsViewModel(stationsProperty=${stationsProperty.value})"
}

