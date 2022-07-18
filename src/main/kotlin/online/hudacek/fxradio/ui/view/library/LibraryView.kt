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

package online.hudacek.fxradio.ui.view.library

import javafx.geometry.Pos
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.util.applySchedulers
import online.hudacek.fxradio.util.applySchedulersObservable
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.*

class LibraryView : BaseView() {

    private val viewModel: LibraryViewModel by inject()

    private val librarySearchView: LibrarySearchView by inject()
    private val libraryListView: LibraryListView by inject()

    override fun onDock() {
        Tables.pinnedCountries
                .selectAll()
                .subscribe {
                    viewModel.pinnedProperty += it
                }
        viewModel.getCountries()
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

                vbox {
                    add(LibraryTitleFragment(messages["library"], viewModel.showLibraryProperty) {
                        viewModel.showLibraryProperty.value = !viewModel.showLibraryProperty.value
                        viewModel.commit()
                    })
                    paddingBottom = 5.0
                }

                vbox {
                    add(libraryListView)
                }

                vbox {
                    vbox {
                        add(LibraryTitleFragment(messages["pinned"], viewModel.showPinnedProperty) {
                            viewModel.showPinnedProperty.value = !viewModel.showPinnedProperty.value
                            viewModel.commit()
                        })
                        paddingBottom = 5.0
                    }

                    add(LibraryCountriesFragment(viewModel.pinnedProperty, viewModel.showPinnedProperty))

                    showWhen {
                        viewModel.pinnedProperty.emptyProperty().not()
                    }
                }
            }
        }

        center {
            vbox {
                vbox {
                    add(LibraryTitleFragment(messages["countries"], viewModel.showCountriesProperty) {
                        viewModel.showCountriesProperty.value = !viewModel.showCountriesProperty.value
                        viewModel.commit()
                    })
                    paddingBottom = 5.0
                }

                vbox {
                    add(LibraryCountriesFragment(viewModel.countriesProperty, viewModel.showCountriesProperty))
                    prefHeightProperty().bind(this@center.heightProperty())

                    showWhen {
                        viewModel.countriesProperty.emptyProperty().not()
                    }
                }

                //Retry link
                vbox(alignment = Pos.CENTER) {
                    hyperlink(messages["downloadRetry"]) {

                        action { viewModel.getCountries() }

                        showWhen {
                            viewModel.countriesProperty.emptyProperty().and(viewModel.showCountriesProperty)
                        }
                    }
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}