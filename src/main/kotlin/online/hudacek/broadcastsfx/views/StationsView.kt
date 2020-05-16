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

package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
import online.hudacek.broadcastsfx.events.LibrarySearchChanged
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.extension.glyph
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * Main view displaying grid of stations
 */
class StationsView : View() {

    private val controller: StationsController by inject()

    private val logger = KotlinLogging.logger {}

    private val searchGlyph = glyph(FontAwesome.Glyph.SEARCH)
    private val errorGlyph = glyph(FontAwesome.Glyph.WARNING)

    private var contentName: Label by singleAssign()

    private val header = label {
        addClass(Styles.header)
    }

    private val subHeader = label {
        addClass(Styles.grayLabel)
    }

    private val headerContainer = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0
        paddingLeft = 10.0
        paddingRight = 10.0
        add(header)
        add(subHeader)
    }

    private val dataGrid: StationsDataGridView by inject()

    private val contentTop = flowpane {
        paddingBottom = 0.0
        maxHeight = 10.0
        style {
            backgroundColor += Color.WHITESMOKE
        }

        contentName = label {
            paddingTop = 8.0
            paddingBottom = 8.0
            paddingLeft = 15.0
            addClass(Styles.subheader)
        }
    }

    init {
        controller.getTopStations()
        subscribe<LibraryRefreshEvent> { event ->
            with(event) {
                when (type) {
                    LibraryType.Country -> {
                        params?.let { controller.getStationsByCountry(it) }
                    }
                    LibraryType.Favourites -> controller.getFavourites()
                    LibraryType.History -> controller.getHistory()
                    else -> {
                        controller.getTopStations()
                    }
                }
            }
        }

        subscribe<LibrarySearchChanged> { event ->
            with(event) {
                if (searchString.length > 2)
                    controller.searchStations(searchString)
                else {
                    contentTop.hide()
                    headerContainer.show()
                    header.text = messages["searchingLibrary"]
                    header.graphic = searchGlyph
                    subHeader.text = messages["searchingLibraryDesc"]
                    dataGrid.hide()
                }
            }
        }
    }

    override val root = vbox {

        style {
            backgroundColor += Color.WHITE
        }

        vgrow = Priority.ALWAYS
        add(headerContainer)
        add(contentTop)
        add(dataGrid)
        dataGrid.root.fitToParentHeight()
    }

    fun showNoResults(queryString: String? = null) {
        contentTop.hide()
        headerContainer.show()
        dataGrid.hide()
        if (queryString != null) {
            subHeader.text = messages["noResultsDesc"]
        }
        header.graphic = null
        header.text =
                if (queryString != null)
                    "${messages["noResultsFor"]} \"$queryString\""
                else {
                    messages["noResults"]
                }
    }

    fun showError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        contentTop.hide()
        headerContainer.show()
        dataGrid.hide()
        header.graphic = errorGlyph
        header.text = messages["connectionError"]
        subHeader.text = messages["connectionErrorDesc"]
    }

    fun showDataGrid(stations: List<Station>) {
        headerContainer.hide()
        contentTop.show()
        dataGrid.show(stations)
    }

    fun setContentName(libraryType: LibraryType, value: String? = null) {
        contentName.apply {
            text = when (libraryType) {
                LibraryType.Favourites -> messages["favourites"]
                LibraryType.History -> messages["history"]
                LibraryType.TopStations -> messages["topStations"]
                LibraryType.Search -> messages["searchResultsFor"] + " \"$value\""
                else -> value
            }
        }
    }
}