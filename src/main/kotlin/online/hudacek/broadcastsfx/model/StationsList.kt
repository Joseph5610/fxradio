package online.hudacek.broadcastsfx.model

import javafx.beans.property.ListProperty
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*

class StationsList {
    val stations = observableListOf(Station.stub())
    val libraryType = LibraryType.TopStations
}

class StationsListModel : ItemViewModel<StationsList>() {
    val stations = bind(StationsList::stations) as ListProperty
    val libraryType = bind(StationsList::libraryType)
}