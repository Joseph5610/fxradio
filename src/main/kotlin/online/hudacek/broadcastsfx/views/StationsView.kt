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
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
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

    private val contentName = label {
        paddingTop = 8.0
        paddingBottom = 8.0
        paddingLeft = 15.0
        addClass(Styles.subheader)
    }

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

        add(contentName)
    }

    init {
        controller.getTopStations()

        //Handle change of stations library
        subscribe<LibraryRefreshEvent> { event ->
            with(event) {
                when (type) {
                    LibraryType.Country -> controller.getStationsByCountry(params)
                    LibraryType.Favourites -> controller.getFavourites()
                    LibraryType.History -> controller.getHistory()
                    LibraryType.Search -> {
                        if (params.length > 2)
                            controller.searchStations(params)
                        else {
                            contentTop.hide()
                            headerContainer.show()
                            header.text = messages["searchingLibrary"]
                            header.graphic = searchGlyph
                            subHeader.text = messages["searchingLibraryDesc"]
                            dataGrid.hide()
                        }
                    }
                    else -> {
                        controller.getTopStations()
                    }
                }
                contentName.text = when (type) {
                    LibraryType.Favourites -> messages["favourites"]
                    LibraryType.History -> messages["history"]
                    LibraryType.TopStations -> messages["topStations"]
                    LibraryType.Search -> messages["searchResultsFor"] + " \"$params\""
                    else -> params
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
}