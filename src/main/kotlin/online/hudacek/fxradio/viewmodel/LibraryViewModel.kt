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

import io.reactivex.disposables.Disposable
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.*
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Countries
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.isValidCountry
import online.hudacek.fxradio.saveProperties
import online.hudacek.fxradio.utils.applySchedulers
import org.controlsfx.control.action.Action
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

enum class LibraryType {
    Favourites, Search, History, Country, TopStations
}

data class LibraryItem(val type: LibraryType, val graphic: FontAwesome.Glyph)

data class SelectedLibrary(val type: LibraryType, val params: String = "")

class LibraryModel(countries: ObservableList<Countries> = observableListOf(),
                   selected: SelectedLibrary = SelectedLibrary(LibraryType.TopStations),
                   searchQuery: String = "", showLibrary: Boolean, showCountries: Boolean) {
    //Countries shown in Countries ListView
    val countries: ObservableList<Countries> by property(countries)

    //Default items shown in library ListView
    val libraries: ObservableList<LibraryItem> by property(observableListOf(
            LibraryItem(LibraryType.TopStations, FontAwesome.Glyph.TROPHY),
            LibraryItem(LibraryType.Favourites, FontAwesome.Glyph.STAR),
            LibraryItem(LibraryType.History, FontAwesome.Glyph.HISTORY)
    ))

    val selected: SelectedLibrary by property(selected)
    val searchQuery: String by property(searchQuery)
    val showLibrary: Boolean by property(showLibrary)
    val showCountries: Boolean by property(showCountries)
}

/**
 * Library view model
 * -------------------
 * Stores shown libraries and countries in the sidebar
 * Used in [online.hudacek.fxradio.views.LibraryView]
 */
class LibraryViewModel : ItemViewModel<LibraryModel>() {

    val countriesProperty = bind(LibraryModel::countries) as ListProperty
    val librariesProperty = bind(LibraryModel::libraries) as ListProperty

    //Currently selected library type
    val selectedProperty = bind(LibraryModel::selected) as ObjectProperty

    val searchQueryProperty = bind(LibraryModel::searchQuery) as StringProperty

    val showLibraryProperty = bind(LibraryModel::showLibrary) as BooleanProperty
    val showCountriesProperty = bind(LibraryModel::showCountries) as BooleanProperty

    init {
        item = LibraryModel(
                searchQuery = Property(Properties.SEARCH_QUERY).get(""),
                showLibrary = Property(Properties.WINDOW_SHOW_LIBRARY).get(true),
                showCountries = Property(Properties.WINDOW_SHOW_COUNTRIES).get(true))
    }

    fun showCountries(): Disposable = StationsApi.service
            .getCountries(CountriesBody())
            .compose(applySchedulers())
            .subscribe({ response ->
                //Ignore invalid states
                val result = response.filter {
                    it.name.length > 1 && it.isValidCountry
                }.asObservable()
                countriesProperty.setAll(result)
            }, {
                fire(NotificationEvent(messages["downloadError"], op = {
                    actions.setAll(Action(messages["retry"]) {
                        showCountries()
                    })
                }))
            })

    override fun onCommit() {
        saveProperties(listOf(
                Pair(Properties.SEARCH_QUERY, searchQueryProperty.value),
                Pair(Properties.WINDOW_SHOW_COUNTRIES, showCountriesProperty.value),
                Pair(Properties.WINDOW_SHOW_LIBRARY, showLibraryProperty.value)
        ))
    }

    fun showSearchResults() = select(SelectedLibrary(LibraryType.Search, searchQueryProperty.value.trim()))

    fun refreshLibrary(libraryType: LibraryType) {
        if (selectedProperty.value.type == libraryType) {
            selectedProperty.value = null
            selectedProperty.value = SelectedLibrary(libraryType)
        }
    }

    fun select(selectedLibrary: SelectedLibrary) {
        selectedProperty.value = selectedLibrary
    }

    fun selected(libraryType: LibraryType): BooleanBinding = selectedProperty.booleanBinding {
        it?.type == libraryType
    }
}