package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.model.AddStationBody
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.property

class AddStationModel(station: AddStationBody,
                      saveToFavourites: Boolean = false,
                      autoCompleteCountries: ObservableList<String> = observableListOf()) {
    val name: String by property(station.name)
    val url: String by property(station.url)
    val homepage: String by property(station.homepage)
    val favicon: String by property(station.favicon)
    val country: String by property(station.country)
    val countryCode: String by property(station.countryCode)
    val state: String by property(station.state)
    val language: String by property(station.language)
    val tags: String by property(station.tags)
    val saveToFavourites: Boolean by property(saveToFavourites)
    val autoCompleteCountries: ObservableList<String> by property(autoCompleteCountries)
}

/**
 * Add Station view model
 * -------------------
 * Stores entered information into the form in [online.hudacek.fxradio.fragments.AddStationFragment]
 * Handles logic for adding new station into the API
 */
class AddStationViewModel : ItemViewModel<AddStationModel>() {
    val name = bind(AddStationModel::name) as StringProperty
    val url = bind(AddStationModel::url) as StringProperty
    val homepage = bind(AddStationModel::homepage) as StringProperty
    val favicon = bind(AddStationModel::favicon) as StringProperty
    val country = bind(AddStationModel::country) as StringProperty
    val countryCode = bind(AddStationModel::countryCode) as StringProperty
    val state = bind(AddStationModel::state) as StringProperty
    val language = bind(AddStationModel::language) as StringProperty
    val tags = bind(AddStationModel::tags) as StringProperty
    val saveToFavouritesProperty = bind(AddStationModel::saveToFavourites) as BooleanProperty
    val autoCompleteCountriesProperty = bind(AddStationModel::autoCompleteCountries) as ListProperty

    init {
        item = AddStationModel(AddStationBody())
    }

    override fun toString(): String {
        return "AddStationViewModel(name=${name.value}," +
                " URL=${url.value}, " +
                "homepage=${homepage.value}, " +
                "favicon=${favicon.value}," +
                "country=${country.value}, " +
                "countryCode=${countryCode.value}, " +
                "state=${state.value}, language=${language.value}, " +
                "tags=${tags.value})"
    }
}