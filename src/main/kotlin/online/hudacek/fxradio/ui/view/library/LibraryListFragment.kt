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

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.geometry.Pos
import online.hudacek.fxradio.api.model.Country
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.ui.flagIcon
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.ui.viewmodel.SelectedLibrary
import online.hudacek.fxradio.ui.viewmodel.SelectedLibraryViewModel
import tornadofx.*

class LibraryListFragment(property: ListProperty<Country>, showProperty: BooleanProperty) : Fragment() {
    private val appEvent: AppEvent by inject()

    private val viewModel: LibraryViewModel by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()

    override val root = listview(property) {
        prefHeightProperty().bind(property.doubleBinding {
            if (it != null) it.size * 30.0 + 10 else 30.0
        })

        cellFormat {
            graphic = hbox(5) {
                imageview {
                    image = it.flagIcon
                }

                val stationWord = if (it.stationcount > 1)
                    messages["stations"] else messages["station"]

                alignment = Pos.CENTER_LEFT

                label(item.name.split("(")[0])

                //Ignore it for pinned stations, they would always have 0 statinocount
                if (it.stationcount > 0) {
                    label("${it.stationcount}") {
                        tooltip("${it.stationcount} $stationWord")
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
                        actionEvents()
                                .map { _ -> it }
                                .subscribe(appEvent.pinCountry)
                    }
                    item(messages["unpin"]) {
                        visibleWhen {
                            viewModel.pinnedProperty.booleanBinding { property ->
                                property?.contains(it)!!
                            }
                        }

                        actionEvents()
                                .map { _ -> it }
                                .subscribe(appEvent.unpinCountry)
                    }
                }
            }
            addClass(Styles.libraryListItem)
        }

        onUserSelect(1) {
            selectedLibraryViewModel.item = SelectedLibrary(LibraryType.Country, it.name)
        }
        showWhen {
            property.emptyProperty().not().and(showProperty)
        }
        addClass(Styles.libraryListView)
    }
}