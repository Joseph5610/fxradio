package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*

class StationHistory {
    val stations = observableListOf<Station>()
}

class StationHistoryModel : ItemViewModel<StationHistory>() {
    val stations = bind(StationHistory::stations)

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