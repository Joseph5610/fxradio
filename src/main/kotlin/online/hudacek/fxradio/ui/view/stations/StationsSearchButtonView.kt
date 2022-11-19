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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import tornadofx.*
import tornadofx.controlsfx.button
import tornadofx.controlsfx.segmentedbutton
import kotlin.collections.set

class StationsSearchButtonView : BaseView() {

    private val viewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    override fun onDock() {
        viewModel.searchByTagProperty.toObservableChangesNonNull()
            .map { LibraryState.Search }
            .subscribe(appEvent.refreshLibrary)
    }

    override val root = segmentedbutton {
        style {
            fontSize = 12.px
        }

        button(messages["search.byName"]) {
            isSelected = true
            // Little hack that allows use to use togglegroup.bind() method
            properties["tornadofx.toggleGroupValue"] = false
            addClass(Styles.segmentedButton)
        }

        button(messages["search.byTag"]) {
            properties["tornadofx.toggleGroupValue"] = true
            addClass(Styles.segmentedButton)
        }
        toggleGroup.bind(viewModel.searchByTagProperty)

        showWhen {
            // Show this view only while Search is current LibraryState
            libraryViewModel.stateProperty.booleanBinding {
                it is LibraryState.Search
            }
        }
    }
}
