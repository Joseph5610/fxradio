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

import io.reactivex.rxjava3.disposables.Disposable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.usecase.country.CountryPinUseCase
import online.hudacek.fxradio.usecase.country.CountryUnpinUseCase
import online.hudacek.fxradio.usecase.country.GetCountriesUseCase
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
    data class SelectedCountry(val country: Country) : LibraryState(country.name)
    object Popular : LibraryState("topStations")
    object Trending : LibraryState("trendingStations")
}

data class LibraryItem(val type: LibraryState, val glyph: FontAwesome.Glyph)

class Library(
    countries: ObservableList<Country> = observableListOf(),
    pinned: ObservableList<Country> = observableListOf(),
    showLibrary: Boolean = Properties.ShowLibrary.value(true),
    showPinned: Boolean = Properties.ShowPinnedCountries.value(true),
    countriesQuery: String = ""
) {

    // Countries shown in Countries ListView
    var countries: ObservableList<Country> by property(countries)

    // Countries shown in Countries ListView
    var pinned: ObservableList<Country> by property(pinned)

    // Default items shown in library ListView
    var libraries: ObservableList<LibraryItem> by property(
        observableListOf(
            LibraryItem(LibraryState.Popular, FontAwesome.Glyph.THUMBS_UP),
            LibraryItem(LibraryState.Trending, FontAwesome.Glyph.FIRE),
            LibraryItem(LibraryState.Favourites, FontAwesome.Glyph.HEART),
        )
    )

    var showLibrary: Boolean by property(showLibrary)
    var showPinned: Boolean by property(showPinned)
    var countriesQuery: String by property(countriesQuery)
}

/**
 * Library view model
 * -------------------
 * Stores shown libraries and countries in the sidebar
 * Used in [online.hudacek.fxradio.ui.view.library.LibraryView]
 */
class LibraryViewModel : BaseStateViewModel<Library, LibraryState>(Library(), LibraryState.Popular) {

    private val getCountriesUseCase: GetCountriesUseCase by inject()
    private val countryPinUseCase: CountryPinUseCase by inject()
    private val countryUnpinUseCase: CountryUnpinUseCase by inject()

    val countriesProperty = bind(Library::countries) as ListProperty
    val librariesProperty = bind(Library::libraries) as ListProperty
    val pinnedProperty = bind(Library::pinned) as ListProperty

    val showLibraryProperty = bind(Library::showLibrary) as BooleanProperty
    val showPinnedProperty = bind(Library::showPinned) as BooleanProperty

    val countriesQueryProperty = bind(Library::countriesQuery) as StringProperty

    fun pinCountry(country: Country): Disposable = countryPinUseCase.execute(country)
        .subscribe({
            pinnedProperty += country
        }, {
            logger.error(it) { "Exception when performing Pinning!" }
        })

    fun unpinCountry(country: Country): Disposable = countryUnpinUseCase.execute(country)
        .subscribe({
            pinnedProperty -= country
        }, {
            logger.error(it) { "Exception when performing Unpinning!" }
        })

    fun getCountries(): Disposable = getCountriesUseCase.execute(Unit).subscribe({
        countriesProperty += it
    }, {
        logger.error(it) { "Exception when downloading countries" }
    })

    override fun onCommit() {
        app.saveProperties(
            mapOf(
                Properties.ShowLibrary to showLibraryProperty.value,
                Properties.ShowPinnedCountries to showPinnedProperty.value
            )
        )
    }
}
