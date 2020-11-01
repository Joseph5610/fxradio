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
import javafx.scene.layout.Priority
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.flagIcon
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.glyph
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.utils.smallLabel
import online.hudacek.fxradio.viewmodel.LibraryType
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedLibrary
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.customTextfield

class LibraryView : View() {

    private val viewModel: LibraryViewModel by inject()

    private val showCountriesListLabel = viewModel.showCountriesProperty.stringBinding {
        if (it!!) messages["hide"] else messages["show"]
    }

    private val showLibraryListLabel = viewModel.showLibraryProperty.stringBinding {
        if (it!!) messages["hide"] else messages["show"]
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
                graphic = glyph(item.graphic, size = 14.0, useStyle = false)
                text = messages[item.type.toString()]
                addClass(Styles.libraryListItem)
            }
            showWhen { viewModel.showLibraryProperty }
            onUserSelect(1) {
                viewModel.select(SelectedLibrary(it.type))
            }
            addClass(Styles.libraryListView)
        }
    }

    private val countriesListView by lazy {
        listview(viewModel.countriesProperty) {
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
                        graphic = imageview(Config.Resources.waveIcon) {
                            fitWidth = 16.0
                            isPreserveRatio = true
                        }
                        addClass(Styles.libraryListItemTag)
                    }
                }
                addClass(Styles.libraryListItem)
            }
            onUserSelect(1) {
                viewModel.select(SelectedLibrary(LibraryType.Country, it.name))
            }
            showWhen {
                viewModel.countriesProperty.emptyProperty().not().and(viewModel.showCountriesProperty)
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

    override val root = borderpane {
        top {
            vbox {
                vbox {
                    add(searchField)
                    style {
                        padding = box(20.px, 10.px, 20.px, 10.px)
                    }
                }

                hbox {
                    smallLabel(messages["library"]) {
                        paddingLeft = 10.0
                    }
                    region { hgrow = Priority.ALWAYS }
                    smallLabel(showLibraryListLabel) {
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
                    smallLabel(showCountriesListLabel) {
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

                add(countriesListView)

                vbox(alignment = Pos.CENTER) {
                    add(retryLink)
                }
                countriesListView.prefHeightProperty().bind(heightProperty())
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
                countriesListView.selectionModel.clearSelection()
            }
            LibraryType.Search -> {
                countriesListView.selectionModel.clearSelection()
                libraryListView.selectionModel.clearSelection()
            }
        }
    }
}