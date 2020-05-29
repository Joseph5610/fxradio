package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*

class StationsHistory {
    val stations = observableListOf<Station>()
}

class StationsHistoryModel : ItemViewModel<StationsHistory>() {
    val stations = bind(StationsHistory::stations)

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