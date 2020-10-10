package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.Database
import tornadofx.*

class StationsHistoryModel(stations: ObservableList<Station> = observableListOf()) {
    val stations: ObservableList<Station> by property(stations)
}

/**
 * Stations History view model
 * -------------------
 * Holds information about last 10 played stations
 * shows in [online.hudacek.fxradio.views.stations.StationsDataGridView] and in MenuBar
 */
class StationsHistoryViewModel : ItemViewModel<StationsHistoryModel>() {
    val stationsProperty = bind(StationsHistoryModel::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValid()) return
        with(stationsProperty) {
            if (!contains(station)) {
                add(station)
                Database.History.add(station)
                        .subscribe()
            }
        }
    }

    fun cleanup() {
        item = StationsHistoryModel()
        Database.History.cleanup()
                .subscribe()
    }
}