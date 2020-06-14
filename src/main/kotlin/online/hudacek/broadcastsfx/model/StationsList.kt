package online.hudacek.broadcastsfx.model

import javafx.beans.property.ListProperty
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*

class StationsList {
    val stations = observableListOf(Station.stub())
}

class StationsListModel : ItemViewModel<StationsList>() {
    val shown = bind(StationsList::stations) as ListProperty
}