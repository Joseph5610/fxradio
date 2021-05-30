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

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.geometry.Pos
import online.hudacek.fxradio.api.stations.model.Country
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.flagIcon
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.*

/**
 * Custom listview fragment for countries
 */
class LibraryCountriesFragment(countriesProperty: ListProperty<Country>,
                               showProperty: BooleanProperty) : BaseFragment() {

    private val viewModel: LibraryViewModel by inject()

    init {
        viewModel
                .stateObservableChanges
                .filter { it !is LibraryState.SelectedCountry }
                .subscribe {
                    root.selectionModel.clearSelection()
                }
    }

    override val root = listview(countriesProperty) {
        /**
         * Set min/max size of listview based on its items size
         */
        prefHeightProperty().bind(countriesProperty.doubleBinding {
            if (it != null) it.size * 30.0 + 5.0 else 30.0
        })

        cellFormat {
            graphic = hbox(5) {
                alignment = Pos.CENTER_LEFT

                imageview {
                    image = it.flagIcon
                }

                label(item.name.split("(")[0])

                //Ignore it for pinned stations, they would always have 0 station count
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

        onUserSelect(1) {
            viewModel.stateProperty.value = LibraryState.SelectedCountry(it)
        }

        showWhen {
            countriesProperty.emptyProperty().not().and(showProperty)
        }

        addClass(Styles.libraryListView)
    }
}