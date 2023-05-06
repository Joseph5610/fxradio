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

import javafx.scene.Node
import javafx.scene.layout.VBox
import javafx.util.Duration
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.openInternalWindow
import online.hudacek.fxradio.util.toObservable
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.center
import tornadofx.fade
import tornadofx.get
import tornadofx.hide
import tornadofx.insets
import tornadofx.label
import tornadofx.listview
import tornadofx.observableListOf
import tornadofx.onUserSelect
import tornadofx.show
import tornadofx.top
import tornadofx.vbox

private const val GLYPH_SIZE = 14.0

class LibraryView : BaseView() {

    private val viewModel: LibraryViewModel by inject()

    private val librarySearchView: LibrarySearchView by inject()
    private val libraryListView: LibraryListView by inject()
    private val libraryPinnedListView: LibraryPinnedListView by inject()

    private val directoryListView by lazy {
        listview(observableListOf(messages["directory.browseAll"])) {
            id = "libraryDirectoryView"
            prefHeight = 40.0
            VBox.setMargin(this, insets(6))

            cellFormat {
                addClass(Styles.libraryListItem)
            }

            cellCache {
                label(it) {
                    graphic = FontAwesome.Glyph.GLOBE.make(GLYPH_SIZE, isPrimary = true)
                }
            }

            onUserSelect(clickCount = 1) {
                Modal.Countries.openInternalWindow()
                selectionModel.clearSelection()
            }

            addClass(Styles.libraryListView)
        }
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

                vbox {
                    add(libraryListView)

                    viewModel.showLibraryProperty
                        .toObservable()
                        .subscribe {
                            customFade(it)
                        }
                }

                vbox {
                    add(
                        find<LibraryTitleFragment>(
                            params = mapOf(
                                "libraryTitle" to messages["pinned.title"],
                                "showProperty" to viewModel.showPinnedProperty
                            )
                        )
                    )

                    showWhen {
                        viewModel.pinnedProperty.emptyProperty().not()
                    }
                }

                vbox {
                    maxHeight = 450.0

                    add(libraryPinnedListView)

                    viewModel.showPinnedProperty
                        .toObservable()
                        .subscribe {
                            customFade(it)
                        }
                }
            }
        }

        center {
            vbox {
                add(
                    find<LibraryTitleFragment>(
                        params = mapOf(
                            "libraryTitle" to messages["directory.title"],
                            "showProperty" to null
                        )
                    )
                )

                add(directoryListView)
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    override fun onDock() {
        // Download list of countries
        viewModel.getCountries()

        Tables.pinnedCountries
            .selectAll()
            .subscribe {
                viewModel.pinnedProperty += it
            }
    }

    /**
     * fades in/out the library listviews when respective showProperty is changed
     */
    private fun Node.customFade(fadeIn: Boolean) {
        val duration = Duration.seconds(0.4)
        if (fadeIn) {
            show()
            fade(duration, 1.0)
        } else {
            fade(duration, 0.0) {
                setOnFinished {
                    hide()
                }
            }
        }
        // save the current state
        viewModel.commit()
    }
}
