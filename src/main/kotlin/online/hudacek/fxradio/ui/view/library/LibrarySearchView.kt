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

import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.platformContextMenu
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.util.searchField
import online.hudacek.fxradio.util.keyCombination
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.get
import tornadofx.insets
import tornadofx.onChange
import tornadofx.validator

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
            alignment = Pos.CENTER
            padding = insets(5, 9)
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
        platformContextMenu(contextMenu.items)
    }

    private fun setSearchState() {
        libraryViewModel.stateProperty.value = LibraryState.Search
    }
}
