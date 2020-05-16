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

import javafx.geometry.Insets
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.controllers.LibraryController
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.extension.smallLabel
import online.hudacek.broadcastsfx.model.rest.Countries
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.customTextfield
import tornadofx.controlsfx.glyph

class LibraryView : View() {

    private val controller: LibraryController by inject()

    private val retryLink = hyperlink(messages["downloadRetry"]) {
        hide()
        action {
            controller.getCountries()
        }
    }

    private val libraryListView = listview(controller.libraryItems) {
        prefHeight = items.size * 30.0 + 5

        cellFormat {
            padding = Insets(5.0, 10.0, 5.0, 15.0)
            graphic = glyph("FontAwesome", item.graphic) {
                color(Styles.colorPrimary)
            }
            text = when (item.type) {
                LibraryType.Favourites -> messages["favourites"]
                LibraryType.Search -> ""
                LibraryType.History -> messages["history"]
                LibraryType.Country -> ""
                LibraryType.TopStations -> messages["topStations"]
            }
            addClass(Styles.customListItem)
        }
        addClass(Styles.libraryListView)
    }

    private val countriesListView = listview<Countries> {
        cellFormat {
            padding = Insets(5.0, 10.0, 5.0, 15.0)
            text = "${item.name} (${item.stationcount})"
            addClass(Styles.customListItem)
        }

        addClass(Styles.libraryListView)
        onUserSelect(1) {
            libraryListView.selectionModel.clearSelection()
            controller.loadLibrary(LibraryType.Country, it.name)
        }
    }

    private val searchField = customTextfield {
        promptText = messages["search"]

        left = label {
            graphic = glyph("FontAwesome", FontAwesome.Glyph.SEARCH) {
                padding = Insets(10.0, 5.0, 10.0, 5.0)
            }
        }
        val savedQuery = app.config.string(Config.Keys.searchQuery)
        savedQuery?.let {
            if (it.isNotBlank()) {
                text = savedQuery
            }
        }

        textProperty().onChange {
            it?.let {
                if (it.length > 80) {
                    text = it.substring(0, 80)
                } else {
                    controller.searchStation(it.trim())
                }
            }

            with(app.config) {
                set(Config.Keys.searchQuery to text)
                save()
            }
        }

        setOnMouseClicked {
            controller.searchStation(text.trim())
        }
    }

    init {
        libraryListView.onUserSelect(1) {
            countriesListView.selectionModel.clearSelection()
            controller.loadLibrary(it.type)
        }
    }

    override val root = vbox {
        vbox {
            prefHeight = 20.0
        }
        vbox {
            paddingAll = 10.0
            add(searchField)
        }

        vbox {
            prefHeight = 20.0
        }
        smallLabel(messages["library"])

        add(libraryListView)
        vbox {
            prefHeight = 20.0
        }

        smallLabel(messages["countries"])

        vbox(alignment = Pos.CENTER) {
            add(retryLink)
            add(countriesListView)
        }
    }

    fun showCountries(countries: List<Countries>) {
        retryLink.hide()
        countriesListView.show()
        countriesListView.items.setAll(countries)
    }

    fun showError() {
        retryLink.show()
        countriesListView.hide()
    }
}