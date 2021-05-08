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
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Colors
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.*

class LibraryListView : BaseView() {

    private val viewModel: LibraryViewModel by inject()

    override fun onDock() {
        //React to changes of library not from by clicking on list item
        viewModel
                .stateObservableChanges
                .subscribe {
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

        prefHeightProperty().bind(viewModel.librariesProperty.doubleBinding {
            if (it != null) it.size * 30.0 + 10 else 30.0
        })

        cellFormat {
            graphic = item.glyph.make(14.0, false, c(Colors.values.libraryIcon))
            text = messages[item.type.key]
            addClass(Styles.libraryListItem)
        }

        showWhen { viewModel.showLibraryProperty }
        onUserSelect(1) {
            viewModel.stateProperty.value = it.type
        }
        addClass(Styles.libraryListView)
    }
}