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
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.*

class StationsModel {
    val stations: ObservableList<Station> by property(observableListOf())
    val stationsViewState: StationsViewState by objectProperty(StationsViewState.NoResults)
}

enum class StationsViewState {
    Normal, Error, Loading, NoResults, ShortQuery
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
    val stationsViewStateProperty = bind(StationsModel::stationsViewState) as ObjectProperty

    //retrieve top voted stations list from endpoint
    val topStations: Disposable
        get() = StationsApi.service
                .getTopStations()
                .compose(applySchedulers())
                .subscribe(::show, ::handleError)

    //retrieve favourites from DB
    val favourites: Disposable
        get() = Database.Favourites.get()
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
            stationsViewStateProperty.value = StationsViewState.ShortQuery
        }
    }

    fun show(stations: List<Station>) {
        stationsProperty.set(stations.asObservable())
        if (stations.isEmpty()) {
            stationsViewStateProperty.value = StationsViewState.NoResults
        } else {
            stationsViewStateProperty.value = StationsViewState.Normal
        }
    }

    fun cleanFavourites() {
        confirm(messages["database.clear.confirm"], messages["database.clear.text"], owner = primaryStage) {
            Database
                    .Favourites.cleanup()
                    .subscribe({
                        fire(NotificationEvent(messages["database.clear.ok"]))
                    }, {
                        logger.error(it) { "Can't remove favourites!" }
                        fire(NotificationEvent(messages["database.clear.error"]))
                    })
        }
    }

    private fun handleError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        stationsViewStateProperty.value = StationsViewState.Error
    }

    override fun toString() = "StationsViewModel(stationsProperty=${stationsProperty.value})"
}

