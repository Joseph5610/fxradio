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

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.apiclient.radiobrowser.model.AddStationRequest
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.usecase.station.StationAddUseCase
import online.hudacek.fxradio.util.toObservable
import tornadofx.FX
import tornadofx.property
import tornadofx.stringBinding
import java.util.Locale

private const val US_LANGUAGE = "en_US"

/**
 * Stores entered information into the form in [online.hudacek.fxradio.ui.fragment.AddStationFragment]
 * Handles logic for adding new station into the API
 */
class AddStationModel(
    name: String = "",
    url: String = "",
    homepage: String = "",
    favicon: String = "",
    country: String = "",
    language: String = "",
    tags: String = "",
    saveToFavourites: Boolean = false
) {
    var name: String by property(name)
    var url: String by property(url)
    var homepage: String by property(homepage)
    var favicon: String by property(favicon)
    var country: String by property(country)
    var language: String by property(language)
    var tags: String by property(tags)
    var saveToFavourites: Boolean by property(saveToFavourites)
}

class AddStationViewModel : BaseViewModel<AddStationModel>(AddStationModel()) {

    private val stationAddUseCase: StationAddUseCase by inject()

    val nameProperty = bind(AddStationModel::name) as StringProperty
    val urlProperty = bind(AddStationModel::url) as StringProperty
    val homePageProperty = bind(AddStationModel::homepage) as StringProperty
    val faviconProperty = bind(AddStationModel::favicon) as StringProperty
    val countryProperty = bind(AddStationModel::country) as StringProperty
    val languageProperty = bind(AddStationModel::language) as StringProperty
    val tagsProperty = bind(AddStationModel::tags) as StringProperty
    val saveToFavouritesProperty = bind(AddStationModel::saveToFavourites) as BooleanProperty

    val saveToFavouritesObservable: Observable<Boolean> = saveToFavouritesProperty.toObservable()

    // Find Country Code from countryProperty value
    private val countryCodeProperty = countryProperty.stringBinding { countryName ->
        isoCountries.find { Locale.of(FX.locale.language, it).displayCountry == countryName }
    }

    // Translate local country and language to US locale
    private val usCountryName = countryCodeProperty.stringBinding { cc ->
        Locale.of(FX.locale.language, cc).getDisplayCountry(usLocale)
    }

    private val usLanguageName = languageProperty.stringBinding { l ->
        isoLanguages
            .map { Locale.of(it) }
            .find { it.displayLanguage == l }
            ?.getDisplayLanguage(usLocale)
    }

    fun addNewStation(): Maybe<Station> =
        stationAddUseCase.execute(convertToRequest())
            .filter { it.ok }
            .map {
                Station(
                    uuid = it.uuid,
                    name = nameProperty.value,
                    urlResolved = urlProperty.value,
                    homepage = homePageProperty.value,
                    favicon = faviconProperty.value,
                    countryCode = countryCodeProperty.value,
                    country = usCountryName.value,
                    language = usLanguageName.value,
                    tags = tagsProperty.value
                )
            }

    private fun convertToRequest() = AddStationRequest(
        nameProperty.value,
        urlProperty.value,
        homePageProperty.value,
        faviconProperty.value,
        countryCodeProperty.value,
        usCountryName.value,
        usLanguageName.value,
        tagsProperty.value
    )

    companion object {
        private val usLocale = Locale.forLanguageTag(US_LANGUAGE)
        private val isoCountries = Locale.getISOCountries()
        private val isoLanguages = Locale.getISOLanguages()
    }
}
