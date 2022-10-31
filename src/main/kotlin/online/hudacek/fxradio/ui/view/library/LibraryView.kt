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
import javafx.scene.layout.Priority
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.paddingAll
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow

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
                    paddingAll = 10.0
                    add(librarySearchView)
                }

                add(LibraryTitleFragment(messages["library"], viewModel.showLibraryProperty) {
                    viewModel.showLibraryProperty.value = !viewModel.showLibraryProperty.value
                    viewModel.commit()
                })

                add(libraryListView)

                add(LibraryTitleFragment(messages["pinned"], viewModel.showPinnedProperty) {
                    viewModel.showPinnedProperty.value = !viewModel.showPinnedProperty.value
                    viewModel.commit()
                })

                vbox {
                    add(LibraryCountriesFragment(viewModel.pinnedProperty, viewModel.showPinnedProperty))

                    showWhen {
                        viewModel.pinnedProperty.emptyProperty().not()
                    }
                }
            }

        }

        center {
            vbox {
                add(LibraryTitleFragment(messages["countries"], viewModel.showCountriesProperty) {
                    viewModel.showCountriesProperty.value = !viewModel.showCountriesProperty.value
                    viewModel.commit()
                })

                vbox {
                    add(LibraryCountriesFragment(viewModel.countriesProperty, viewModel.showCountriesProperty))
                    maxHeightProperty().bind(this@center.heightProperty())

                    showWhen {
                        viewModel.countriesProperty.emptyProperty().not()
                    }
                }

                vbox(alignment = Pos.CENTER) {
                    hyperlink(messages["downloadRetry"]) {

                        action { viewModel.getCountries() }

                        showWhen {
                            viewModel.countriesProperty.emptyProperty().and(viewModel.showCountriesProperty)
                        }
                        addClass(Styles.primaryTextColor)
                    }
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}
