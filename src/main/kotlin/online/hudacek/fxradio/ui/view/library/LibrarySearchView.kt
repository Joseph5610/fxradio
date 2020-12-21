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

import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.make
import online.hudacek.fxradio.utils.searchField
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * Search input field view
 */
class LibrarySearchView : View() {

    private val viewModel: LibraryViewModel by inject()

    override val root = searchField(messages["search"], viewModel.bindSearchQueryProperty) {
        id = "search"

        left = label {
            graphic = FontAwesome.Glyph.SEARCH.make(size = 14.0)
        }

        //Fire up search results after input is written to text field
        textProperty().onChange {
            if (text.length >= 50) {
                text = text.substring(0, 49)
            }
            viewModel.showSearchResults()
            viewModel.commit()
        }

        setOnMouseClicked {
            viewModel.showSearchResults()
        }

        validator {
            when {
                it!!.isNotEmpty() && it.length < 3 -> error(messages["searchingLibraryDesc"])
                it.length >= 49 -> error(messages["field.max.length"])
                else -> null
            }
        }
    }
}