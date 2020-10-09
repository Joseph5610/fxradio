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

import griffon.javafx.support.flagicons.FlagIcon
import javafx.geometry.Pos
import mu.KotlinLogging
import online.hudacek.fxradio.api.model.countryCode
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.glyph
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.utils.smallLabel
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedLibrary
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.customTextfield

class LibraryView : View() {

    private val logger = KotlinLogging.logger {}

    private val viewModel: LibraryViewModel by inject()

    private val retryLink = hyperlink(messages["downloadRetry"]) {
        action {
            viewModel.showCountries()
        }
        showWhen { viewModel.countriesProperty.sizeProperty.isEqualTo(0) }
    }

    private val libraryListView = listview(viewModel.librariesProperty) {
        id = "libraryListView"
        cellFormat {
            graphic = glyph(item.graphic, size = 14.0, useStyle = false)
            text = messages[item.type.toString()]
            addClass(Styles.libraryListItem)
        }

        addClass(Styles.libraryListView)
    }

    private val countriesListView = listview(viewModel.countriesProperty) {
        cellFormat {
            graphic = hbox(5) {

                item.countryCode?.let {
                    try {
                        imageview {
                            image = FlagIcon(it)
                        }
                    } catch (e: Exception) {
                        logger.debug { "Exception while displaying country flag" }
                    }
                }

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
            viewModel.selectedProperty.value = SelectedLibrary(LibraryType.Country, it.name)
        }
        showWhen {
            viewModel.countriesProperty.sizeProperty.isNotEqualTo(0)
        }
    }

    override fun onDock() {
        viewModel.showCountries()

        with(libraryListView) {
            prefHeight = viewModel.librariesProperty.size * 30.0 + 10
            selectionModel.select(viewModel.librariesProperty[0])
        }

        libraryListView.onUserSelect(1) {
            countriesListView.selectionModel.clearSelection()
            viewModel.selectedProperty.value = SelectedLibrary(it.type)
        }
    }

    private val searchField = customTextfield {
        promptText = messages["search"]
        id = "search"

        bind(viewModel.searchQueryProperty)

        left = label {
            graphic = glyph(FontAwesome.Glyph.SEARCH, size = 14.0)
        }

        //Fire up search results after input is written to text field
        textProperty().onChange {
            if (text.length >= 50) {
                text = text.substring(0, 49)
            } else {
                it?.let(viewModel::handleSearch)
            }
        }

        setOnMouseClicked {
            viewModel.handleSearchInputClick()
            countriesListView.selectionModel.clearSelection()
            libraryListView.selectionModel.clearSelection()
        }

        validator {
            if (it!!.length >= 49) error(messages["field.max.length"]) else null
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
        addClass(Styles.backgroundWhiteSmoke)
    }
}