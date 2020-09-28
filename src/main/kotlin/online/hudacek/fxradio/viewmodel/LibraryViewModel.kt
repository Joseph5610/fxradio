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
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Countries
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.utils.applySchedulers
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

data class LibraryItem(val type: LibraryType, val graphic: FontAwesome.Glyph)

data class SelectedLibrary(val type: LibraryType, val params: String = "")

class LibraryModel(countries: ObservableList<Countries> = observableListOf()) {
    //Countries shown in Countries ListView
    val countries: ObservableList<Countries> by property(countries)

    //Default items shown in library ListView
    val libraries: ObservableList<LibraryItem> by property(observableListOf(
            LibraryItem(LibraryType.TopStations, FontAwesome.Glyph.TROPHY),
            LibraryItem(LibraryType.Favourites, FontAwesome.Glyph.STAR),
            LibraryItem(LibraryType.History, FontAwesome.Glyph.HISTORY)
    ))

    val selected: SelectedLibrary by property(SelectedLibrary(LibraryType.TopStations))
    val isError by booleanProperty()
}

/**
 * Library view model
 * -------------------
 * Stores shown libraries and countries in the sidebar
 * Used in [online.hudacek.fxradio.views.LibraryView]
 */
class LibraryViewModel : ItemViewModel<LibraryModel>() {

    val savedQuery
        get() = app.config.string(Config.Keys.searchQuery)

    val countriesProperty = bind(LibraryModel::countries) as ListProperty
    val librariesProperty = bind(LibraryModel::libraries) as ListProperty

    //Currently selected library type
    val selectedProperty = bind(LibraryModel::selected) as ObjectProperty

    val isErrorProperty = bind(LibraryModel::isError) as BooleanProperty

    fun showCountries(): Disposable = StationsApi.service
            .getCountries(CountriesBody())
            .compose(applySchedulers())
            .subscribe({ response ->
                //Ignore invalid states
                val result = response.filter {
                    it.name.length > 1 && !it.name.contains(".")
                }.asObservable()
                countriesProperty.setAll(result)
                isErrorProperty.value = false
            }, {
                isErrorProperty.value = true
            })

    fun handleSearch(searchedValue: String) {
        if (searchedValue.length < 80) {
            handleSearchInputClick(searchedValue)
            with(app.config) {
                set(Config.Keys.searchQuery to searchedValue)
                save()
            }
        }
    }

    fun handleSearchInputClick(text: String) {
        selectedProperty.value = SelectedLibrary(LibraryType.Search, text.trim())
    }
}
