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
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.property
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryModel
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.showWhen
import tornadofx.*

class LibraryView : View() {

    private val viewModel: LibraryViewModel by inject()

    private val librarySearchView: LibrarySearchView by inject()
    private val libraryCountriesListView: LibraryCountriesListView by inject()
    private val libraryListView: LibraryListView by inject()

    override fun onDock() {
        viewModel.item = LibraryModel(
                searchQuery = property(Properties.SEARCH_QUERY, ""),
                showLibrary = property(Properties.WINDOW_SHOW_LIBRARY, true),
                showCountries = property(Properties.WINDOW_SHOW_COUNTRIES, true))
        viewModel.showCountries()
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

                add(LibraryTitleFragment(messages["library"], viewModel.showLibraryProperty) {
                    viewModel.showLibraryProperty.value = !viewModel.showLibraryProperty.value
                    viewModel.commit()
                })
                add(libraryListView)
            }
        }

        center {
            vbox {
                add(LibraryTitleFragment(messages["countries"], viewModel.showCountriesProperty) {
                    viewModel.showCountriesProperty.value = !viewModel.showCountriesProperty.value
                    viewModel.commit()
                })

                add(libraryCountriesListView)

                //Retry link
                vbox(alignment = Pos.CENTER) {
                    hyperlink(messages["downloadRetry"]) {
                        action {
                            viewModel.showCountries()
                        }
                        showWhen {
                            viewModel.countriesProperty.emptyProperty()
                                    .and(viewModel.showCountriesProperty)
                        }
                    }
                }
                libraryCountriesListView.root.prefHeightProperty().bind(heightProperty())
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}