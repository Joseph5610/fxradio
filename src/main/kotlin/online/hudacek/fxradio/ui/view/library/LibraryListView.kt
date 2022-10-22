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

package online.hudacek.fxradio.ui.view.library

import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.ListViewHandler
import online.hudacek.fxradio.viewmodel.DarkModeViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.addClass
import tornadofx.c
import tornadofx.doubleBinding
import tornadofx.get
import tornadofx.label
import tornadofx.listview
import tornadofx.onUserSelect

private const val GLYPH_SIZE = 14.0

class LibraryListView : BaseView() {

    private val viewModel: LibraryViewModel by inject()
    private val darkModeViewModel: DarkModeViewModel by inject()
    override fun onDock() {
        // React to changes of library not from by clicking on list item
        viewModel.stateObservableChanges.subscribe {
            if (it is LibraryState.SelectedCountry || it is LibraryState.Search) {
                root.selectionModel.clearSelection()
            } else {
                val selectedListItem = root.items.find { list -> list.type == it }
                selectedListItem?.let { item ->
                    root.selectionModel.select(item)
                }
            }
        }
    }

    override val root = listview(viewModel.librariesProperty) {
        id = "libraryListView"

        val handler = ListViewHandler(this)
        setOnKeyPressed(handler::handle)

        prefHeightProperty().bind(viewModel.librariesProperty.doubleBinding {
            if (it != null) it.size * 30.0 + 10 else 30.0
        })

        cellFormat {
            addClass(Styles.libraryListItem)
        }

        cellCache {
            label(messages[it.type.key]) {
                graphic = it.glyph.make(GLYPH_SIZE, c(darkModeViewModel.appearanceProperty.value!!.primary))
            }
        }

        showWhen { viewModel.showLibraryProperty }
        onUserSelect(1) {
            viewModel.stateProperty.value = it.type
        }

        addClass(Styles.libraryListView)
    }
}
