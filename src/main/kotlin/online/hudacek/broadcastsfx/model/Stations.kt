package online.hudacek.broadcastsfx.model

import javafx.beans.property.ListProperty
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*

class Stations {
    val stations = observableListOf(Station.stub())
}

class StationsModel : ItemViewModel<Stations>() {
    val stationsProperty = bind(Stations::stations) as ListProperty
}