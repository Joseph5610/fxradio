package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.ItemViewModel
import tornadofx.onChange
import tornadofx.property

class CurrentStation(station: Station) {
    var station: Station by property(station)
}

class CurrentStationModel : ItemViewModel<CurrentStation>() {
    private val stationHistory: StationHistoryModel by inject()

    val station = bind(CurrentStation::station)

    init {
        station.onChange {
            if (it != null) {
                stationHistory.add(it)
            }
        }
    }
}