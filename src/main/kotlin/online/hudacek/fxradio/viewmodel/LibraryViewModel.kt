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

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Countries
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.events.LibraryTypeChanged
import online.hudacek.fxradio.events.LibraryType
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

data class LibraryItem(val type: LibraryType, val graphic: FontAwesome.Glyph)

class LibraryModel {
    //Countries shown in Countries ListView
    val countries: ObservableList<Countries> by property(observableListOf())

    //Default items shown in library ListView
    val libraries: ObservableList<LibraryItem> by property(observableListOf(
            LibraryItem(LibraryType.TopStations, FontAwesome.Glyph.TROPHY),
            LibraryItem(LibraryType.Favourites, FontAwesome.Glyph.STAR),
            LibraryItem(LibraryType.History, FontAwesome.Glyph.HISTORY)
    ))
}

class LibraryViewModel : ItemViewModel<LibraryModel>() {

    val savedQuery
        get() = app.config.string(Config.Keys.searchQuery)

    val countriesListProperty = bind(LibraryModel::countries) as ListProperty
    val librariesListProperty = bind(LibraryModel::libraries) as ListProperty

    val isError = booleanProperty()

    init {
        item = LibraryModel()
        //Load Countries List
        showCountries()
    }

    fun showCountries(): Disposable = StationsApi.client
            .getCountries(CountriesBody())
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ response ->
                //Ignore invalid states
                val result = response.filter {
                    it.name.length > 1 && !it.name.contains(".")
                }.asObservable()
                countriesListProperty.setAll(result)
                isError.value = false
            }, {
                isError.value = true
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

    fun handleSearchInputClick(text: String) = fire(LibraryTypeChanged(LibraryType.Search, text.trim()))
}
