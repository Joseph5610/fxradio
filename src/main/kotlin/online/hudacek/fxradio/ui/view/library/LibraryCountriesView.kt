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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.ui.viewmodel.SelectedLibraryViewModel
import tornadofx.View
import tornadofx.vbox

class LibraryCountriesView : View() {

    private val viewModel: LibraryViewModel by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()

    private val listViewFragment = LibraryListFragment(viewModel.countriesProperty, viewModel.showCountriesProperty)

    init {
        selectedLibraryViewModel.itemProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .filter { it.type != LibraryType.Country }
                .subscribe { listViewFragment.root.selectionModel.clearSelection() }
    }

    override val root = vbox {
        add(listViewFragment)
    }
}