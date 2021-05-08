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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import javafx.geometry.Pos
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import tornadofx.*
import tornadofx.controlsfx.button
import tornadofx.controlsfx.segmentedbutton

class StationsHeaderSearchView : BaseView() {

    private val viewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    override fun onDock() {
        viewModel.searchByTagProperty
                .toObservableChangesNonNull()
                .map { LibraryState.Search }
                .subscribe(appEvent.refreshLibrary)
    }

    override val root = vbox(alignment = Pos.CENTER) {
        hbox {
            segmentedbutton {
                style {
                    fontSize = 12.px
                }
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
                toggleGroup.bind(viewModel.searchByTagProperty)
            }

            showWhen {
                //Show only while Search results are shown
                libraryViewModel.stateProperty.booleanBinding {
                    it is LibraryState.Search
                }
            }
        }
    }
}