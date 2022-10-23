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

import javafx.scene.input.KeyCode
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.searchField
import online.hudacek.fxradio.util.keyCombination
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

private const val searchGlyphSize = 14.0
private const val searchMaxLength = 49.0

/**
 * Search input field view
 */
class LibrarySearchView : BaseView() {

    private val viewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    override val root = searchField(messages["search"], viewModel.bindQueryProperty) {
        id = "search"

        left = FontAwesome.Glyph.SEARCH.make(searchGlyphSize, isPrimary = false) {
            padding = insets(5, 10)
        }

        setOnMouseClicked {
            setSearchState()
        }

        textProperty().onChange {
            setSearchState()
            viewModel.commit()
        }

        shortcut(keyCombination(KeyCode.F)) {
            requestFocusOnSceneAvailable()
            setSearchState()
        }

        validator {
            when {
                it != null && it.length >= searchMaxLength -> error(messages["field.max.length"])
                else -> null
            }
        }
    }

    private fun setSearchState() {
        libraryViewModel.stateProperty.value = LibraryState.Search
    }
}
