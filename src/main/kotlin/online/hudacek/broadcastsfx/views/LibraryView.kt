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

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import javafx.collections.ObservableList
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
import tornadofx.controlsfx.left
import tornadofx.controlsfx.statusbar

class LibraryView : View() {

    private val controller: LibraryController by inject()

    private val retryLink = hyperlink(messages["downloadRetry"]) {
        hide()
        action {
            controller.getCountries()
        }
    }

    private val libraryListView = listview(controller.libraryItems) {
        prefHeight = items.size * 30.0 + 10

        cellFormat {
            graphic = glyph("FontAwesome", item.graphic)
            text = when (item.type) {
                LibraryType.Favourites -> messages["favourites"]
                LibraryType.Search -> ""
                LibraryType.History -> messages["history"]
                LibraryType.Country -> ""
                LibraryType.TopStations -> messages["topStations"]
            }
            addClass(Styles.libraryListItem)
        }
        addClass(Styles.libraryListView)
    }

    private val countriesListView = listview<Countries> {
        cellFormat {
            graphic = hbox(5) {
                val stationWord = if (item.stationcount > 1)
                    messages["stations"] else messages["station"]

                alignment = Pos.CENTER_LEFT
                label(item.name.split("(")[0])
                label("${item.stationcount} $stationWord") {
                    addClass(Styles.libraryListItemTag)
                }
            }

            addClass(Styles.libraryListItem)
        }

        addClass(Styles.libraryListView)
        onUserSelect(1) {
            libraryListView.selectionModel.clearSelection()
            controller.loadLibrary(LibraryType.Country, it.name)
        }
    }

    private val searchField = customTextfield {
        promptText = messages["search"]
        id = "search"

        left = label {
            graphic = glyph("FontAwesome", FontAwesome.Glyph.SEARCH) {
                style {
                    padding = box(10.px, 5.px)
                }
            }
        }

        app.config.string(Config.Keys.searchQuery)?.let {
            if (it.isNotBlank()) {
                text = it
            }
        }

        //Fire up search results after input is written to text field
        textProperty().toObservableChanges()
                .filter { it.newVal.length < 80 }
                .map { it.newVal.trim() }
                .subscribe {
                    controller.loadLibrary(LibraryType.Search, it)
                    with(app.config) {
                        set(Config.Keys.searchQuery to text)
                        save()
                    }
                }

        setOnMouseClicked {
            controller.loadLibrary(LibraryType.Search, text.trim())
            countriesListView.selectionModel.clearSelection()
            libraryListView.selectionModel.clearSelection()
        }
    }

    init {
        //Load Countries List
        controller.getCountries()

        //set default view
        libraryListView.selectionModel.select(0)
        controller.loadLibrary(LibraryType.TopStations)

        libraryListView.onUserSelect(1) {
            countriesListView.selectionModel.clearSelection()
            controller.loadLibrary(it.type)
        }
    }

    override val root = vbox {
        vbox {
            add(searchField)
            style {
                padding = box(20.px, 10.px, 20.px, 10.px)
            }
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

        statusbar {
            left {
                label {
                    countriesListView.itemsProperty().onChange {
                        text = "${it?.size} ${messages["countries"]}"
                    }
                    addClass(Styles.grayLabel)
                }
            }
        }
    }

    fun showCountries(countries: ObservableList<Countries>) {
        retryLink.hide()
        countriesListView.show()
        countriesListView.itemsProperty().set(countries)
    }

    fun showError() {
        retryLink.show()
        countriesListView.hide()
    }
}