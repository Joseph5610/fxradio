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

package online.hudacek.fxradio.views.stations

import javafx.geometry.Pos
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedLibrary
import online.hudacek.fxradio.viewmodel.StationsViewModel
import online.hudacek.fxradio.viewmodel.StationsViewState
import tornadofx.*

/**
 * Bar with opened library name and action button within stationsView
 */
class StationsHeaderView : View() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    //Bindings for library name based on selected library
    private val libraryNameTextProperty = libraryViewModel.selectedProperty.stringBinding {
        it?.let {
            if (it.type == LibraryType.Country) it.params
            else if (it.type == LibraryType.Search) messages[it.type.toString()] + " \"${it.params}\""
            else messages[it.type.toString()]
        }
    }

    private val libraryName by lazy {
        label(libraryNameTextProperty) {
            paddingTop = 8.0
            paddingBottom = 8.0
            addClass(Styles.subheader)
        }
    }

    override val root = borderpane {
        padding = insets(horizontal = 10.0, vertical = 0.0)
        maxHeight = 10.0

        left {
            add(libraryName)
        }

        right {
            vbox(alignment = Pos.CENTER_RIGHT) {
                button(messages["favourites.clean"]) {
                    action {
                        viewModel.cleanFavourites()
                    }
                    showWhen {
                        libraryViewModel.selectedProperty.isEqualTo(SelectedLibrary(LibraryType.Favourites))
                    }
                }
            }
        }

        showWhen {
            viewModel.stationsViewStateProperty.isEqualTo(StationsViewState.Normal)
        }

        addClass(Styles.backgroundWhiteSmoke)
    }
}
