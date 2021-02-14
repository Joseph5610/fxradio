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

import online.hudacek.fxradio.ui.style.Colors
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.ui.viewmodel.SelectedLibrary
import online.hudacek.fxradio.utils.make
import online.hudacek.fxradio.utils.showWhen
import tornadofx.*

class LibraryListView : View() {

    private val viewModel: LibraryViewModel by inject()

    init {
        //React to changes of library not from by clicking on list item
        viewModel.selectedPropertyChanges
                .subscribe {
                    if (it.type == LibraryType.Country || it.type == LibraryType.Search) {
                        root.selectionModel.clearSelection()
                    } else {
                        val selectedListItem = root.items.find { list -> list.type == it.type }
                        selectedListItem?.let { item ->
                            root.selectionModel.select(item)
                        }
                    }
                }
    }

    override val root = listview(viewModel.librariesProperty) {
        prefHeightProperty().bind(viewModel.librariesProperty.doubleBinding {
            if (it != null) it.size * 30.0 + 10 else 30.0
        })

        id = "libraryListView"
        cellFormat {
            graphic = item.graphic.make(14.0, false, c(Colors.values.libraryIcon))
            text = messages[item.type.toString()]
            addClass(Styles.libraryListItem)
        }
        showWhen { viewModel.showLibraryProperty }
        onUserSelect(1) {
            viewModel.selectedProperty.value = SelectedLibrary(it.type)
        }
        addClass(Styles.libraryListView)
    }
}