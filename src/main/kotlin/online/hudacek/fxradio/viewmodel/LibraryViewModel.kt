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
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.stations.model.Country
import online.hudacek.fxradio.usecase.CountryPinUseCase
import online.hudacek.fxradio.usecase.CountryUnpinUseCase
import online.hudacek.fxradio.usecase.GetCountriesUseCase
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.saveProperties
import online.hudacek.fxradio.util.value
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.observableListOf
import tornadofx.property

private val logger = KotlinLogging.logger {}

sealed class LibraryState(val key: String) {
    object Favourites : LibraryState("favourites")
    object Search : LibraryState("Search")
    object History : LibraryState("history")
    data class SelectedCountry(val country: Country) : LibraryState(country.name)
    object TopStations : LibraryState("topStations")
}

data class LibraryItem(val type: LibraryState, val glyph: FontAwesome.Glyph)

class Library(countries: ObservableList<Country> = observableListOf(),
              pinned: ObservableList<Country> = observableListOf(),
              showLibrary: Boolean = Properties.ShowLibrary.value(true),
              showCountries: Boolean = Properties.ShowCountries.value(true),
              showPinned: Boolean = Properties.ShowPinnedCountries.value(true)) {

    //Countries shown in Countries ListView
    var countries: ObservableList<Country> by property(countries)

    //Countries shown in Countries ListView
    var pinned: ObservableList<Country> by property(pinned)

    //Default items shown in library ListView
    var libraries: ObservableList<LibraryItem> by property(observableListOf(
            LibraryItem(LibraryState.TopStations, FontAwesome.Glyph.TROPHY),
            LibraryItem(LibraryState.Favourites, FontAwesome.Glyph.STAR),
            LibraryItem(LibraryState.History, FontAwesome.Glyph.HISTORY)
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
class LibraryViewModel : BaseStateViewModel<Library, LibraryState>(Library(), LibraryState.TopStations) {

    private val getCountriesUseCase: GetCountriesUseCase by inject()
    private val countryPinUseCase: CountryPinUseCase by inject()
    private val countryUnpinUseCase: CountryUnpinUseCase by inject()

    val countriesProperty = bind(Library::countries) as ListProperty
    val librariesProperty = bind(Library::libraries) as ListProperty
    val pinnedProperty = bind(Library::pinned) as ListProperty

    val showLibraryProperty = bind(Library::showLibrary) as BooleanProperty
    val showCountriesProperty = bind(Library::showCountries) as BooleanProperty
    val showPinnedProperty = bind(Library::showPinned) as BooleanProperty

    fun pinCountry(country: Country): Disposable = countryPinUseCase.execute(country)
            .subscribe({
                pinnedProperty.add(it)
            }, {
                logger.error(it) { "Exception when performing Pinning!" }
            })

    fun unpinCountry(country: Country): Disposable = countryUnpinUseCase.execute(country)
            .subscribe({
                pinnedProperty -= it
            }, {
                logger.error(it) { "Exception when performing Unpinning!" }
            })

    fun getCountries() = getCountriesUseCase.execute(countriesProperty)

    override fun onCommit() {
        app.saveProperties(mapOf(
                Properties.ShowCountries to showCountriesProperty.value,
                Properties.ShowLibrary to showLibraryProperty.value,
                Properties.ShowPinnedCountries to showPinnedProperty.value
        ))
    }
}