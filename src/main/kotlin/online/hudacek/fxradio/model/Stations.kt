package online.hudacek.fxradio.model

import javafx.beans.property.ListProperty
import online.hudacek.fxradio.model.rest.Station
import tornadofx.*

class Stations {
    val stations = observableListOf(Station.stub())
}

class StationsModel : ItemViewModel<Stations>() {
    val stationsProperty = bind(Stations::stations) as ListProperty
}