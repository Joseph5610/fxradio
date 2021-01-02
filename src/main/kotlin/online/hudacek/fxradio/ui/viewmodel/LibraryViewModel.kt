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

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.Properties
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
    Favourites, Search, SearchByTag, History, Country, TopStations
}

data class LibraryItem(val type: LibraryType, val graphic: FontAwesome.Glyph)

data class SelectedLibrary(val type: LibraryType, val params: String = "")

class LibraryModel(countries: ObservableList<Countries> = observableListOf(),
                   selected: SelectedLibrary = SelectedLibrary(LibraryType.TopStations),
                   searchQuery: String = "",
                   showLibrary: Boolean = true,
                   showCountries: Boolean = true) {

    //Countries shown in Countries ListView
    var countries: ObservableList<Countries> by property(countries)

    //Default items shown in library ListView
    var libraries: ObservableList<LibraryItem> by property(observableListOf(
            LibraryItem(LibraryType.TopStations, FontAwesome.Glyph.TROPHY),
            LibraryItem(LibraryType.Favourites, FontAwesome.Glyph.STAR),
            LibraryItem(LibraryType.History, FontAwesome.Glyph.HISTORY)
    ))

    var selected: SelectedLibrary by property(selected)
    var searchQuery: String by property(searchQuery)
    var showLibrary: Boolean by property(showLibrary)
    var showCountries: Boolean by property(showCountries)
    var useTagSearch: Boolean by property(false)
}

/**
 * Library view model
 * -------------------
 * Stores shown libraries and countries in the sidebar
 * Used in [online.hudacek.fxradio.ui.view.library.LibraryView]
 */
class LibraryViewModel : ItemViewModel<LibraryModel>(LibraryModel()) {

    val countriesProperty = bind(LibraryModel::countries) as ListProperty
    val librariesProperty = bind(LibraryModel::libraries) as ListProperty

    //Currently selected library type
    val selectedProperty = bind(LibraryModel::selected) as ObjectProperty
    val selectedPropertyChanges: Observable<SelectedLibrary> = selectedProperty
            .toObservableChanges()
            .map { it.newVal }

    val showLibraryProperty = bind(LibraryModel::showLibrary) as BooleanProperty
    val showCountriesProperty = bind(LibraryModel::showCountries) as BooleanProperty
    val useTagSearchProperty = bind(LibraryModel::useTagSearch) as BooleanProperty

    //Internal only, contains unedited search query
    val bindSearchQueryProperty = bind(LibraryModel::searchQuery) as StringProperty

    //Search query is limited to 50 chars and trimmed to reduce requests to API
    val searchQueryProperty = bindSearchQueryProperty.stringBinding {
        if (it != null) {
            if (it.length > 50) it.substring(0, 50).trim() else it.trim()
        } else ""
    }

    val refreshLibrary = BehaviorSubject.create<LibraryType>()

    init {
        refreshLibrary
                .filter { selectedProperty.value.type == it }
                .subscribe {
                    selectedProperty.value = null
                    selectedProperty.value = SelectedLibrary(it)
                }
    }

    fun showCountries(): Disposable = StationsApi.service
            .getCountries(CountriesBody())
            .compose(applySchedulers())
            .subscribe({ response ->
                //Ignore invalid states
                val result = response.filter { it.isValidCountry }.asObservable()
                countriesProperty.setAll(result)
            }, {
                fire(NotificationEvent(messages["downloadError"], op = {
                    actions.setAll(Action(messages["retry"]) {
                        showCountries()
                    })
                }))
            })

    fun showSearchResults() {
        if (useTagSearchProperty.value) {
            selectedProperty.value = SelectedLibrary(LibraryType.SearchByTag)
        } else {
            selectedProperty.value = SelectedLibrary(LibraryType.Search)
        }
    }

    override fun onCommit() {
        saveProperties(mapOf(
                Properties.SEARCH_QUERY to bindSearchQueryProperty.value,
                Properties.WINDOW_SHOW_COUNTRIES to showCountriesProperty.value,
                Properties.WINDOW_SHOW_LIBRARY to showLibraryProperty.value
        ))
    }
}