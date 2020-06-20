package online.hudacek.fxradio.model

import online.hudacek.fxradio.model.rest.AddStationBody
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
    val name = bind(AddStation::name)
    val URL = bind(AddStation::URL)
    val homepage = bind(AddStation::homepage)
    val favicon = bind(AddStation::favicon)
    val country = bind(AddStation::country)
    val countryCode = bind(AddStation::countryCode)
    val state = bind(AddStation::state)
    val language = bind(AddStation::language)
    val tags = bind(AddStation::tags)
}