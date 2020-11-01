package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.Database
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.property

class HistoryModel(stations: ObservableList<Station> = observableListOf()) {
    val stations: ObservableList<Station> by property(stations)
}

/**
 * Stations History view model
 * -------------------
 * Holds information about last 10 played stations
 * shows in [online.hudacek.fxradio.views.stations.StationsDataGridView] and in MenuBar
 */
class HistoryViewModel : ItemViewModel<HistoryModel>() {
    val stationsProperty = bind(HistoryModel::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValid()) return
        with(stationsProperty) {
            if (!contains(station)) {
                add(station)
                Database.history
                        .insert(station)
                        .subscribe()
            }
        }
    }

    fun cleanup() {
        item = HistoryModel()
        Database.history
                .delete()
                .subscribe()
    }
}