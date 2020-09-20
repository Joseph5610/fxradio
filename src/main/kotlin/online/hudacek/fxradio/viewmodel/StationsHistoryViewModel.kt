package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ListProperty
import online.hudacek.fxradio.api.model.Station
import tornadofx.*

class StationsHistoryModel {
    val stations = observableListOf<Station>()
}

class StationsHistoryViewModel : ItemViewModel<StationsHistoryModel>() {
    val stationsProperty = bind(StationsHistoryModel::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValidStation()) return
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