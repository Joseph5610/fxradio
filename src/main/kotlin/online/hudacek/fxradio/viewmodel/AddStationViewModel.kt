/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.apiclient.stations.model.StationBody
import online.hudacek.fxradio.usecase.AddStationUseCase
import tornadofx.property
import tornadofx.stringBinding
import java.util.Locale

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

    fun addStation() = addStationUseCase.execute(
            StationBody(
                    nameProperty.value,
                    urlProperty.value,
                    homePageProperty.value,
                    faviconProperty.value,
                    countryCodeProperty.value,
                    countryProperty.value,
                    languageProperty.value,
                    tagsProperty.value
            )
    )

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
