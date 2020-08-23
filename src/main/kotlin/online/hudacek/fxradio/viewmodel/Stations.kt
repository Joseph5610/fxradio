package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ListProperty
import online.hudacek.fxradio.api.model.Station
import tornadofx.*

class Stations {
    val stations = observableListOf(Station.stub())
}

class StationsModel : ItemViewModel<Stations>() {
    val stationsProperty = bind(Stations::stations) as ListProperty
}