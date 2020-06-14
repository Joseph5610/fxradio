package online.hudacek.broadcastsfx.model

import javafx.beans.property.ListProperty
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*

class StationsHistory {
    val stations = observableListOf<Station>()
}

class StationsHistoryModel : ItemViewModel<StationsHistory>() {
    val stations = bind(StationsHistory::stations) as ListProperty

    fun add(station: Station) {
        if (!station.isValidStation()) return
        with(stations.value) {
            if (!contains(station)) {
                if (size > 10) {
                    removeAt(0)
                }
                add(station)
            }
        }
    }
}