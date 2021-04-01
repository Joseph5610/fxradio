/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.ui.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.AppEvent
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.property
import tornadofx.stringBinding
import java.util.*

/**
 * Add Station view model
 * -------------------
 * Stores entered information into the form in [online.hudacek.fxradio.ui.modal.AddStationFragment]
 * Handles logic for adding new station into the API
 */
class AddStation(name: String = "",
                 url: String = "",
                 homepage: String = "",
                 favicon: String = "",
                 country: String = "",
                 language: String = "",
                 tags: String = "",
                 uuid: String = "",
                 saveToFavourites: Boolean = false,
                 countriesList: ObservableList<String> = observableListOf()) {
    var name: String by property(name)
    var url: String by property(url)
    var homepage: String by property(homepage)
    var favicon: String by property(favicon)
    var country: String by property(country)
    var language: String by property(language)
    var tags: String by property(tags)
    var saveToFavourites: Boolean by property(saveToFavourites)
    var countriesList: ObservableList<String> by property(countriesList)
    var uuid: String by property(uuid)
}

class AddStationViewModel : ItemViewModel<AddStation>(AddStation()) {

    private val appEvent: AppEvent by inject()

    val nameProperty = bind(AddStation::name) as StringProperty
    val urlProperty = bind(AddStation::url) as StringProperty
    val homePageProperty = bind(AddStation::homepage) as StringProperty
    val faviconProperty = bind(AddStation::favicon) as StringProperty
    val countryProperty = bind(AddStation::country) as StringProperty
    val languageProperty = bind(AddStation::language) as StringProperty
    val tagsProperty = bind(AddStation::tags) as StringProperty
    val uuidProperty = bind(AddStation::uuid) as StringProperty

    val saveToFavouritesProperty = bind(AddStation::saveToFavourites) as BooleanProperty
    val countriesListProperty = bind(AddStation::countriesList) as ListProperty

    //Retrieve countryCode value from entered country name
    val countryCodeProperty = countryProperty.stringBinding { countryName ->
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
    }

    override fun onCommit() {
        saveToFavouritesProperty
                .toObservable()
                .filter { it }
                .map {
                    Station(stationuuid = uuidProperty.value,
                            name = nameProperty.value,
                            url_resolved = urlProperty.value,
                            homepage = homePageProperty.value,
                            favicon = faviconProperty.value,
                            countrycode = countryCodeProperty.value,
                            country = countryProperty.value,
                            language = languageProperty.value,
                            tags = tagsProperty.value)
                }
                .subscribe(appEvent.addFavourite)
    }
}