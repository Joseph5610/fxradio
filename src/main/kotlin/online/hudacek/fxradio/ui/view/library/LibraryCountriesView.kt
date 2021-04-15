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

import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.vbox

/**
 * View that shows listview with main library types
 */
class LibraryCountriesView : BaseView() {

    private val viewModel: LibraryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val listViewFragment by lazy {
        LibraryCountriesListFragment(viewModel.countriesProperty, viewModel.showCountriesProperty)
    }

    override fun onDock() {
        libraryViewModel
                .stateObservableChanges()
                .filter { it !is LibraryState.IsCountry }
                .subscribe { listViewFragment.root.selectionModel.clearSelection() }
    }

    override val root = vbox {
        add(listViewFragment)
    }
}