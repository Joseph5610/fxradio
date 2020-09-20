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

package online.hudacek.fxradio.views

import javafx.geometry.Pos
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.LibraryTypeChanged
import online.hudacek.fxradio.extension.showWhen
import online.hudacek.fxradio.extension.smallLabel
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.viewmodel.LibraryModel
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.customTextfield
import tornadofx.controlsfx.glyph

class LibraryView : View() {

    private val viewModel: LibraryViewModel by inject()

    private val retryLink = hyperlink(messages["downloadRetry"]) {
        action {
            viewModel.showCountries()
        }
        showWhen { viewModel.isError }
    }

    private val libraryListView = listview(viewModel.librariesListProperty) {
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

    private val countriesListView = listview(viewModel.countriesListProperty) {
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
            fire(LibraryTypeChanged(LibraryType.Country, it.name))
        }
        showWhen {
            viewModel.isError.not()
        }
    }

    init {
        viewModel.item = LibraryModel()
        viewModel.showCountries()

        fire(LibraryTypeChanged(LibraryType.TopStations))

        libraryListView.onUserSelect(1) {
            countriesListView.selectionModel.clearSelection()
            fire(LibraryTypeChanged(it.type))
        }

        viewModel.librariesListProperty.onChangeOnce {
            if (it != null) {
                with(libraryListView) {
                    prefHeight = it.size * 30.0 + 10
                    selectionModel.select(it[0])
                }
            }
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

        viewModel.savedQuery?.let {
            if (it.isNotBlank()) {
                text = it
            }
        }

        //Fire up search results after input is written to text field
        textProperty().onChange {
            it?.let(viewModel::handleSearch)
        }

        setOnMouseClicked {
            viewModel.handleSearchInputClick(text)
            countriesListView.selectionModel.clearSelection()
            libraryListView.selectionModel.clearSelection()
        }
    }

    override val root = borderpane {
        top {
            vbox {
                vbox {
                    add(searchField)
                    style {
                        padding = box(20.px, 10.px, 20.px, 10.px)
                    }
                }

                smallLabel(messages["library"])
                add(libraryListView)
            }
        }

        center {
            vbox {
                smallLabel(messages["countries"])
                vbox(alignment = Pos.CENTER) {
                    add(countriesListView)
                    add(retryLink)
                }
            }
        }
    }
}