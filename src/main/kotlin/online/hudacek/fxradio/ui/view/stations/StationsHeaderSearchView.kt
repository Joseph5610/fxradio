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

package online.hudacek.fxradio.ui.view.stations

import javafx.geometry.Pos
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.showWhen
import tornadofx.*
import tornadofx.controlsfx.button
import tornadofx.controlsfx.segmentedbutton

class StationsHeaderSearchView : View() {

    private val libraryViewModel: LibraryViewModel by inject()

    init {
        libraryViewModel.useTagSearchProperty.onChange {
            libraryViewModel.showSearchResults()
        }
    }

    override val root = vbox(alignment = Pos.CENTER) {
        hbox {
            segmentedbutton {
                button(messages["search.byName"]) {
                    isSelected = true
                    //Little hack that allows use to use togglegroup.bind() method
                    properties["tornadofx.toggleGroupValue"] = false
                    addClass(Styles.coloredButton)
                }

                button(messages["search.byTag"]) {
                    properties["tornadofx.toggleGroupValue"] = true
                    addClass(Styles.coloredButton)
                }
                toggleGroup.bind(libraryViewModel.useTagSearchProperty)
            }

            showWhen {
                //Show only while Search results are shown
                libraryViewModel.selectedProperty.booleanBinding {
                    it?.type == LibraryType.Search || it?.type == LibraryType.SearchByTag
                }
            }
        }
    }
}