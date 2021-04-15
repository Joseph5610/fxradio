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

import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

/**
 * Bar with opened library name and action button within stationsView
 */
class StationsHeaderView : BaseView() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()

    private val stationsHeaderSearchView: StationsHeaderSearchView by inject()

    //Bindings for library name based on selected library
    private val libraryNameTextProperty = libraryViewModel.stateProperty.stringBinding {
        it?.let {
            when (it) {
                is LibraryState.IsCountry -> it.country.name
                is LibraryState.Search -> messages["Search"]
                else -> messages[it.key]
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
                        libraryViewModel.stateProperty.booleanBinding {
                            it is LibraryState.Search
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
            viewModel.stateProperty.isNotEqualTo(StationsState.ShortQuery)
        }

        addClass(Styles.backgroundWhiteSmoke)
    }
}
