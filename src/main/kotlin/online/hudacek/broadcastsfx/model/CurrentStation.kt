package online.hudacek.broadcastsfx.model

import tornadofx.*

class CurrentStation(station: Station) {
    var station by property(station)
    fun stationProperty() = getProperty(CurrentStation::station)
}

class StationViewModel : ItemViewModel<CurrentStation>() {
    val station = bind { item?.stationProperty() }
}