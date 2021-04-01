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

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.CountriesBody
import online.hudacek.fxradio.api.model.Country
import online.hudacek.fxradio.api.model.isValid
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.applySchedulers
import online.hudacek.fxradio.utils.saveProperties
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

enum class LibraryType {
    Favourites, Search, History, Country, TopStations
}

data class LibraryItem(val type: LibraryType, val graphic: FontAwesome.Glyph)

class Library(countries: ObservableList<Country> = observableListOf(),
              pinned: ObservableList<Country> = observableListOf(),
              showLibrary: Boolean = true,
              showCountries: Boolean = true,
              showPinned: Boolean = true) {

    //Countries shown in Countries ListView
    var countries: ObservableList<Country> by property(countries)

    //Countries shown in Countries ListView
    var pinned: ObservableList<Country> by property(pinned)

    //Default items shown in library ListView
    var libraries: ObservableList<LibraryItem> by property(observableListOf(
            LibraryItem(LibraryType.TopStations, FontAwesome.Glyph.TROPHY),
            LibraryItem(LibraryType.Favourites, FontAwesome.Glyph.STAR),
            LibraryItem(LibraryType.History, FontAwesome.Glyph.HISTORY)
    ))

    var showLibrary: Boolean by property(showLibrary)
    var showCountries: Boolean by property(showCountries)
    var showPinned: Boolean by property(showPinned)
}

/**
 * Library view model
 * -------------------
 * Stores shown libraries and countries in the sidebar
 * Used in [online.hudacek.fxradio.ui.view.library.LibraryView]
 */
class LibraryViewModel : ItemViewModel<Library>(Library()) {
    private val appEvent: AppEvent by inject()

    val countriesProperty = bind(Library::countries) as ListProperty
    val librariesProperty = bind(Library::libraries) as ListProperty
    val pinnedProperty = bind(Library::pinned) as ListProperty

    val showLibraryProperty = bind(Library::showLibrary) as BooleanProperty
    val showCountriesProperty = bind(Library::showCountries) as BooleanProperty
    val showPinnedProperty = bind(Library::showPinned) as BooleanProperty

    init {
        appEvent.pinCountry
                .filter { !pinnedProperty.contains(it) }
                .flatMapSingle { Tables.pinnedCountries.insert(it) }
                .subscribe({
                    pinnedProperty.add(it)
                    appEvent.appNotification.onNext(
                            AppNotification(messages["pinned.message"].formatted(it.name),
                                    FontAwesome.Glyph.CHECK))
                }, {
                    appEvent.appNotification.onNext(
                            AppNotification(messages["pinning.error"],
                                    FontAwesome.Glyph.WARNING))
                })

        appEvent.unpinCountry
                .filter { pinnedProperty.contains(it) }
                .flatMapSingle { Tables.pinnedCountries.remove(it) }
                .subscribe({
                    pinnedProperty.remove(it)
                    appEvent.appNotification.onNext(
                            AppNotification(messages["unpinned.message"].formatted(it.name),
                                    FontAwesome.Glyph.CHECK))
                }, {
                    appEvent.appNotification.onNext(
                            AppNotification(messages["pinning.error"],
                                    FontAwesome.Glyph.WARNING))
                })

        appEvent.refreshCountries
                .flatMapSingle {
                    StationsApi.service
                            .getCountries(CountriesBody())
                            .compose(applySchedulers())
                }
                //Ignore invalid states
                .map { list -> list.filter { it.isValid }.asObservable() }
                .subscribe({
                    countriesProperty.setAll(it)
                }, {
                    appEvent.appNotification.onNext(
                            AppNotification(messages["downloadError"],
                                    FontAwesome.Glyph.WARNING))
                })
    }

    override fun onCommit() {
        saveProperties(mapOf(
                Properties.WINDOW_SHOW_COUNTRIES to showCountriesProperty.value,
                Properties.WINDOW_SHOW_LIBRARY to showLibraryProperty.value,
                Properties.WINDOW_SHOW_PINNED to showPinnedProperty.value
        ))
    }
}