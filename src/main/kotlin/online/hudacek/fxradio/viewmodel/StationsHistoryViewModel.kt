package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ListProperty
import online.hudacek.fxradio.api.model.Station
import tornadofx.*

class StationsHistoryModel {
    val stations = observableListOf<Station>()
}

/**
 * Stations History view model
 * -------------------
 * Holds information about last 10 played stations
 * shows in [online.hudacek.fxradio.views.StationsDataGridView] and in MenuBar
 */
class StationsHistoryViewModel : ItemViewModel<StationsHistoryModel>() {
    val stationsProperty = bind(StationsHistoryModel::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValid()) return
        with(stationsProperty) {
            if (!contains(station)) {
                if (size > 10) {
                    removeAt(0)
                }
                add(station)
            }
        }
    }
}