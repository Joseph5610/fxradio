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

import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.searchField
import online.hudacek.fxradio.ui.viewmodel.*
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.property
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.View
import tornadofx.get
import tornadofx.label
import tornadofx.validator

/**
 * Search input field view
 */
class LibrarySearchView : View() {

    private val viewModel: SearchViewModel by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()

    init {
        viewModel.item = Search(query = property(Properties.SEARCH_QUERY, ""))
    }

    override val root = searchField(messages["search"], viewModel.bindQueryProperty) {
        id = "search"

        left = label {
            graphic = FontAwesome.Glyph.SEARCH.make(size = 14.0)
        }

        setOnMouseClicked {
            selectedLibraryViewModel.item = SelectedLibrary(LibraryType.Search)
        }

        validator {
            when {
                it != null && it.length >= 49 -> error(messages["field.max.length"])
                else -> null
            }
        }
    }
}