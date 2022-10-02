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

package online.hudacek.fxradio.ui.view.stations

import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import online.hudacek.fxradio.viewmodel.StationsState
import online.hudacek.fxradio.viewmodel.StationsViewModel
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.borderpane
import tornadofx.get
import tornadofx.hbox
import tornadofx.insets
import tornadofx.label
import tornadofx.left
import tornadofx.paddingBottom
import tornadofx.paddingTop
import tornadofx.right
import tornadofx.stringBinding

/**
 * Bar with opened library name and action button within stationsView
 */
class StationsHeaderView : BaseView() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()

    private val stationsHeaderSearchView: StationsHeaderSearchView by inject()

    // Bindings for library name based on selected library
    private val libraryNameTextProperty = libraryViewModel.stateProperty.stringBinding {
        it?.let {
            when (it) {
                is LibraryState.SelectedCountry -> it.key
                else -> messages[it.key]
            }
        }
    }

    override val root = borderpane {
        padding = insets(horizontal = 20.0, vertical = 2.0)
        maxHeight = 10.0

        left {
            hbox(spacing = 5) {
                label(libraryNameTextProperty) {
                    paddingTop = 8.0
                    paddingBottom = 8.0
                    addClass(Styles.subheader)
                }

                label(searchViewModel.queryBinding) {
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
            // This view is shown always, except when clicking on empty search TextField
            viewModel.stateProperty.isNotEqualTo(StationsState.ShortQuery)
        }

        addClass(Styles.backgroundWhiteSmoke)
    }
}
