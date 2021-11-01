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

package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.api.stations.model.Station
import online.hudacek.fxradio.api.stations.model.StationBody
import online.hudacek.fxradio.usecase.AddStationUseCase
import tornadofx.property
import tornadofx.stringBinding
import java.util.*

/**
 * Stores entered information into the form in [online.hudacek.fxradio.ui.fragment.AddStationFragment]
 * Handles logic for adding new station into the API
 */
class AddStationModel(name: String = "",
                      url: String = "",
                      homepage: String = "",
                      favicon: String = "",
                      country: String = "",
                      language: String = "",
                      tags: String = "",
                      uuid: String = "",
                      saveToFavourites: Boolean = false) {
    var name: String by property(name)
    var url: String by property(url)
    var homepage: String by property(homepage)
    var favicon: String by property(favicon)
    var country: String by property(country)
    var language: String by property(language)
    var tags: String by property(tags)
    var saveToFavourites: Boolean by property(saveToFavourites)
    var uuid: String by property(uuid)
}

class AddStationViewModel : BaseViewModel<AddStationModel>(AddStationModel()) {

    private val addStationUseCase: AddStationUseCase by inject()

    val nameProperty = bind(AddStationModel::name) as StringProperty
    val urlProperty = bind(AddStationModel::url) as StringProperty
    val homePageProperty = bind(AddStationModel::homepage) as StringProperty
    val faviconProperty = bind(AddStationModel::favicon) as StringProperty
    val countryProperty = bind(AddStationModel::country) as StringProperty
    val languageProperty = bind(AddStationModel::language) as StringProperty
    val tagsProperty = bind(AddStationModel::tags) as StringProperty
    val uuidProperty = bind(AddStationModel::uuid) as StringProperty

    val saveToFavouritesProperty = bind(AddStationModel::saveToFavourites) as BooleanProperty

    //Find Country Code from countryProperty value
    private val countryCodeProperty = countryProperty.stringBinding { countryName ->
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
    }

    fun addStation() = addStationUseCase.execute(StationBody(
            nameProperty.value,
            urlProperty.value,
            homePageProperty.value,
            faviconProperty.value,
            countryCodeProperty.value,
            countryProperty.value,
            languageProperty.value,
            tagsProperty.value
    ))

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