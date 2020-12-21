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

package online.hudacek.fxradio.ui.view.library

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import online.hudacek.fxradio.api.model.flagIcon
import online.hudacek.fxradio.ui.style.ColorValues
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.ui.viewmodel.SelectedLibrary
import online.hudacek.fxradio.utils.make
import online.hudacek.fxradio.utils.searchField
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.utils.smallLabel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class LibraryView : View() {

    private val viewModel: LibraryViewModel by inject()

    private val librarySearchView: LibrarySearchView by inject()
    private val libraryCountriesView: LibraryCountriesView by inject()

    private val showLibraryListLabel = viewModel.showLibraryProperty.objectBinding {
        showIcon(it!!)
    }

    private val showCountriesListLabel = viewModel.showCountriesProperty.objectBinding {
        showIcon(it!!)
    }

    private val retryLink by lazy {
        hyperlink(messages["downloadRetry"]) {
            action {
                viewModel.showCountries()
            }
            showWhen { viewModel.countriesProperty.emptyProperty() }
        }
    }

    private val libraryListView by lazy {
        listview(viewModel.librariesProperty) {
            id = "libraryListView"
            cellFormat {
                graphic = item.graphic.make(14.0, false, c("#d65458"))
                text = messages[item.type.toString()]
                addClass(Styles.libraryListItem)
            }
            showWhen { viewModel.showLibraryProperty }
            onUserSelect(1) {
                viewModel.selectedProperty.value = SelectedLibrary(it.type)
            }
            addClass(Styles.libraryListView)
        }
    }

    init {
        viewModel.selectedProperty.onChange {
            handleSelectionChange(it)
        }
    }

    override fun onDock() {
        viewModel.showCountries()

        with(libraryListView) {
            prefHeight = viewModel.librariesProperty.size * 30.0 + 10
            selectionModel.select(viewModel.librariesProperty[0])
        }
    }

    override val root = borderpane {
        top {
            vbox {
                vbox {
                    add(librarySearchView)
                    style {
                        padding = box(20.px, 10.px, 20.px, 10.px)
                    }
                }

                hbox {
                    smallLabel(messages["library"]) {
                        paddingLeft = 10.0
                    }
                    region { hgrow = Priority.ALWAYS }
                    smallLabel {
                        graphicProperty().bind(showLibraryListLabel)
                        paddingLeft = 10.0
                        paddingRight = 10.0

                        setOnMouseClicked {
                            viewModel.showLibraryProperty.value = !viewModel.showLibraryProperty.value
                            viewModel.commit()
                        }

                        showWhen {
                            this@hbox.hoverProperty()
                        }
                    }
                }
                add(libraryListView)
            }
        }

        center {
            vbox {
                hbox {
                    smallLabel(messages["countries"]) {
                        paddingLeft = 10.0
                    }
                    region { hgrow = Priority.ALWAYS }
                    smallLabel {
                        graphicProperty().bind(showCountriesListLabel)
                        paddingLeft = 10.0
                        paddingRight = 10.0

                        setOnMouseClicked {
                            viewModel.showCountriesProperty.value = !viewModel.showCountriesProperty.value
                            viewModel.commit()
                        }

                        showWhen {
                            this@hbox.hoverProperty()
                        }
                    }
                }

                add(libraryCountriesView)

                vbox(alignment = Pos.CENTER) {
                    add(retryLink)
                }

                libraryCountriesView.root.prefHeightProperty().bind(heightProperty())
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    private fun handleSelectionChange(it: SelectedLibrary?) {
        when (it?.type) {
            LibraryType.Country -> {
                libraryListView.selectionModel.clearSelection()
            }
            LibraryType.Favourites, LibraryType.History, LibraryType.TopStations -> {
                libraryCountriesView.root.selectionModel.clearSelection()
            }
            LibraryType.Search, LibraryType.SearchByTag -> {
                libraryCountriesView.root.selectionModel.clearSelection()
                libraryListView.selectionModel.clearSelection()
            }
        }
    }

    private fun showIcon(show: Boolean) = if (show)
        FontAwesome.Glyph.CHEVRON_DOWN.make(size = 11.0, useStyle = false, color = c(ColorValues().grayLabel))
    else
        FontAwesome.Glyph.CHEVRON_RIGHT.make(size = 11.0, useStyle = false, color = c(ColorValues().grayLabel))
}

/**
 * Search input field view
 */
class LibrarySearchView : View() {

    private val viewModel: LibraryViewModel by inject()

    override val root = searchField(messages["search"], viewModel.bindSearchQueryProperty) {
        id = "search"

        left = label {
            graphic = FontAwesome.Glyph.SEARCH.make(size = 14.0)
        }

        //Fire up search results after input is written to text field
        textProperty().onChange {
            if (text.length >= 50) {
                text = text.substring(0, 49)
            }
            viewModel.showSearchResults()
            viewModel.commit()
        }

        setOnMouseClicked {
            viewModel.showSearchResults()
        }

        validator {
            when {
                it!!.isNotEmpty() && it.length < 3 -> error(messages["searchingLibraryDesc"])
                it.length >= 49 -> error(messages["field.max.length"])
                else -> null
            }
        }
    }
}

class LibraryCountriesView : View() {

    private val viewModel: LibraryViewModel by inject()

    override val root = listview(viewModel.countriesProperty) {
        cellFormat {
            graphic = hbox(5) {
                imageview {
                    image = item.flagIcon
                }

                val stationWord = if (item.stationcount > 1)
                    messages["stations"] else messages["station"]

                alignment = Pos.CENTER_LEFT
                label(item.name.split("(")[0])
                label("${item.stationcount}") {
                    tooltip("${item.stationcount} $stationWord")
                    addClass(Styles.libraryListItemTag)
                }
            }
            addClass(Styles.libraryListItem)
        }
        onUserSelect(1) {
            viewModel.selectedProperty.value = SelectedLibrary(LibraryType.Country, it.name)
        }
        showWhen {
            viewModel.countriesProperty.emptyProperty().not().and(viewModel.showCountriesProperty)
        }
        addClass(Styles.libraryListView)
    }
}