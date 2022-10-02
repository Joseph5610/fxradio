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

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.geometry.Pos
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.flagIcon
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.contextmenu
import tornadofx.doubleBinding
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.item
import tornadofx.label
import tornadofx.listview
import tornadofx.onUserSelect
import tornadofx.visibleWhen

/**
 * Custom listview fragment for countries
 */
class LibraryCountriesFragment(countriesProperty: ListProperty<Country>, showProperty: BooleanProperty)
    : BaseFragment() {

    private val viewModel: LibraryViewModel by inject()

    init {
        viewModel.stateObservableChanges.filter { it !is LibraryState.SelectedCountry }.subscribe {
            root.selectionModel.clearSelection()
        }
    }

    override val root = listview(countriesProperty) {
        id = "libraryCountriesFragment"

        /**
         * Set min/max size of listview based on its items size
         */
        prefHeightProperty().bind(countriesProperty.doubleBinding {
            if (it != null) it.size * 30.0 + 10.0 else 30.0
        })

        cellFormat {
            graphic = hbox(spacing = 5) {
                alignment = Pos.CENTER_LEFT

                imageview {
                    image = it.flagIcon
                }

                label(item.name.split("(")[0])

                // Do not show count of stations for pinned stations, they would always show 0
                // as we do not store this in DB
                if (it.stationcount > 0) {
                    label("${it.stationcount}") {
                        addClass(Styles.libraryListItemTag)
                    }
                }

                contextmenu {
                    item(messages["pin"]) {
                        visibleWhen {
                            viewModel.pinnedProperty.booleanBinding { property ->
                                !property?.contains(it)!!
                            }
                        }
                        action {
                            viewModel.pinCountry(it)
                        }
                    }

                    item(messages["unpin"]) {
                        visibleWhen {
                            viewModel.pinnedProperty.booleanBinding { property ->
                                property?.contains(it)!!
                            }
                        }
                        action {
                            viewModel.unpinCountry(it)
                        }
                    }
                }
            }
            addClass(Styles.libraryListItem)
        }

        onUserSelect(clickCount = 1) {
            viewModel.stateProperty.value = LibraryState.SelectedCountry(it)
        }

        showWhen {
            countriesProperty.emptyProperty().not().and(showProperty)
        }

        addClass(Styles.libraryListView)
    }
}
