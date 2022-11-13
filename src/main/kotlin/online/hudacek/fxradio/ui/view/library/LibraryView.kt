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
                    padding = insets(10, 20)
                    add(librarySearchView)
                }

                add(
                    find<LibraryTitleFragment>(
                        params = mapOf(
                            "libraryTitle" to messages["library"],
                            "showProperty" to viewModel.showLibraryProperty
                        )
                    )
                )

                add(libraryListView)

                add(
                    find<LibraryTitleFragment>(
                        params = mapOf(
                            "libraryTitle" to messages["pinned"],
                            "showProperty" to viewModel.showPinnedProperty
                        )
                    )
                )

                vbox {
                    add(
                        find<LibraryCountriesFragment>(
                            params = mapOf("countriesProperty" to viewModel.pinnedProperty)
                        )
                    )

                    showWhen {
                        viewModel.pinnedProperty.emptyProperty().not().and(viewModel.showPinnedProperty)
                    }
                }
            }

        }

        center {
            vbox {
                add(
                    find<LibraryTitleFragment>(
                        params = mapOf(
                            "libraryTitle" to messages["countries"],
                            "showProperty" to viewModel.showCountriesProperty
                        )
                    )
                )
                vbox {
                    vgrow = Priority.ALWAYS
                    add(
                        find<LibraryCountriesFragment>(
                            params = mapOf("countriesProperty" to viewModel.countriesProperty)
                        )
                    )
                    prefHeightProperty().bind(this@center.heightProperty().minus(10.0))

                    showWhen {
                        viewModel.countriesProperty.emptyProperty().not().and(viewModel.showCountriesProperty)
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
