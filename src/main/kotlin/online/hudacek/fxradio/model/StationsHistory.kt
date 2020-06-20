package online.hudacek.fxradio.model

import javafx.beans.property.ListProperty
import online.hudacek.fxradio.model.rest.Station
import tornadofx.*

class StationsHistory {
    val stations = observableListOf<Station>()
}

class StationsHistoryModel : ItemViewModel<StationsHistory>() {
    val stations = bind(StationsHistory::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValidStation()) return
        with(stations) {
            if (!contains(station)) {
                if (size > 10) {
                    removeAt(0)
                }
                add(station)
            }
        }
    }
}