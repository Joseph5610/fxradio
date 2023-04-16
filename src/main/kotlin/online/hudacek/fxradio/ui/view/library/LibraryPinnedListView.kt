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
import javafx.scene.layout.VBox
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.item
import online.hudacek.fxradio.ui.menu.platformContextMenu
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.ListViewHandler
import online.hudacek.fxradio.ui.util.flagIcon
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.doubleBinding
import tornadofx.fitToParentHeight
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.listview
import tornadofx.onUserSelect
import tornadofx.selectedItem
import tornadofx.stringBinding
import java.util.*

/**
 * Custom listview view for pinned countries
 */
class LibraryPinnedListView : BaseView() {

    private val viewModel: LibraryViewModel by inject()

    override fun onDock() {
        viewModel.stateObservable.subscribe {
            if (it !is LibraryState.SelectedCountry) {
                root.selectionModel.clearSelection()
            } else {
                if (it.country.iso3166 != root.selectedItem?.iso3166) {
                    root.selectionModel.clearSelection()
                }
            }
        }
    }

    override val root = listview(viewModel.pinnedProperty) {
        id = "libraryCountriesView"

        fitToParentHeight()

        VBox.setMargin(this, insets(6))
        val handler = ListViewHandler(this)
        setOnKeyPressed(handler::handle)

        /**
         * Set min/max size of listview based on its items size
         */
        prefHeightProperty().bind(viewModel.pinnedProperty.doubleBinding {
            if (it != null) it.size * 30.0 + 15.0 else 10.0
        })

        cellCache {
            hbox(spacing = 5, alignment = Pos.CENTER_LEFT) {
                val countryName = Locale.of("", it.iso3166).displayName

                imageview {
                    image = it.flagIcon
                }

                label(countryName)

                platformContextMenu(
                    listOf(item(messages["pin"]) {
                        val itemName = viewModel.pinnedProperty.stringBinding { l ->
                            if (l?.contains(it)!!)
                                messages["unpin"]
                            else
                                messages["pin"]
                        }
                        textProperty().bind(itemName)

                        action {
                            if (viewModel.pinnedProperty.contains(it)) {
                                viewModel.unpinCountry(it)
                            } else {
                                viewModel.pinCountry(it)
                            }
                        }
                    })
                )
            }
        }
        cellFormat {
            addClass(Styles.libraryListItem)
        }

        onUserSelect(clickCount = 1) {
            viewModel.stateProperty.value = LibraryState.SelectedCountry(it)
        }

        addClass(Styles.libraryListView)
    }
}
