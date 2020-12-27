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
import online.hudacek.fxradio.api.model.flagIcon
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.ui.viewmodel.SelectedLibrary
import online.hudacek.fxradio.utils.showWhen
import tornadofx.*

class LibraryCountriesListView : View() {

    private val viewModel: LibraryViewModel by inject()

    init {
        viewModel.selectedPropertyChanges
                .filter { it.type != LibraryType.Country }
                .subscribe { root.selectionModel.clearSelection() }
    }

    override val root = listview(viewModel.countriesProperty) {
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
                    addClass(Styles.libraryListItemTag)
                }
            }
            addClass(Styles.libraryListItem)
        }

        onUserSelect(1) {
            viewModel.selectedProperty.value = SelectedLibrary(LibraryType.Country, it.name)
        }
        showWhen {
            viewModel.countriesProperty.emptyProperty().not().and(viewModel.showCountriesProperty)
        }
        addClass(Styles.libraryListView)
    }
}