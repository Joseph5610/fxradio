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

import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.*
import tornadofx.*

/**
 * Bar with opened library name and action button within stationsView
 */
class StationsHeaderView : View() {

    private val viewModel: StationsViewModel by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()

    private val stationsHeaderSearchView: StationsHeaderSearchView by inject()

    //Bindings for library name based on selected library
    private val libraryNameTextProperty = selectedLibraryViewModel.itemProperty.stringBinding {
        it?.let {
            when (it.type) {
                LibraryType.Country -> it.libraryOption
                LibraryType.Search -> messages["Search"]
                else -> messages[it.type.toString()]
            }
        }
    }

    override val root = borderpane {
        padding = insets(horizontal = 10.0, vertical = 0.0)
        maxHeight = 10.0

        left {
            hbox(5) {
                label(libraryNameTextProperty) {
                    paddingTop = 8.0
                    paddingBottom = 8.0
                    addClass(Styles.subheader)
                }

                label(searchViewModel.queryProperty) {
                    paddingTop = 8.0
                    paddingBottom = 8.0
                    showWhen {
                        selectedLibraryViewModel.itemProperty.booleanBinding {
                            it?.type == LibraryType.Search
                        }
                    }
                    addClass(Styles.grayTextColor)
                    addClass(Styles.subheader)
                }
            }
        }

        right {
            add(stationsHeaderSearchView)
        }

        showWhen {
            //This view is shown always, except when clicking on empty search textfield
            viewModel.viewStateProperty.isNotEqualTo(StationsViewState.ShortQuery)
        }

        addClass(Styles.backgroundWhiteSmoke)
    }
}
