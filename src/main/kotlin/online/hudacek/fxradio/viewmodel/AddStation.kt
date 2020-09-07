package online.hudacek.fxradio.viewmodel

import javafx.beans.property.StringProperty
import online.hudacek.fxradio.api.model.AddStationBody
import tornadofx.*

class AddStation(station: AddStationBody) {
    val name: String by property(station.name)
    val URL: String by property(station.URL)
    val homepage: String by property(station.homepage)
    val favicon: String by property(station.favicon)
    val country: String by property(station.country)
    val countryCode: String by property(station.countryCode)
    val state: String by property(station.state)
    val language: String by property(station.language)
    val tags: String by property(station.tags)
}

class AddStationModel : ItemViewModel<AddStation>() {
    val name = bind(AddStation::name) as StringProperty
    val URL = bind(AddStation::URL) as StringProperty
    val homepage = bind(AddStation::homepage) as StringProperty
    val favicon = bind(AddStation::favicon) as StringProperty
    val country = bind(AddStation::country) as StringProperty
    val countryCode = bind(AddStation::countryCode) as StringProperty
    val state = bind(AddStation::state) as StringProperty
    val language = bind(AddStation::language) as StringProperty
    val tags = bind(AddStation::tags) as StringProperty
}