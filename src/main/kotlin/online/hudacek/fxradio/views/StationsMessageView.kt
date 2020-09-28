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
package online.hudacek.fxradio.views

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.glyph
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.StationsViewModel
import online.hudacek.fxradio.viewmodel.StationsViewState
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsMessageView : View() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val searchGlyph = glyph(FontAwesome.Glyph.SEARCH)
    private val errorGlyph = glyph(FontAwesome.Glyph.WARNING)
    private val loadingGlyph = glyph(FontAwesome.Glyph.SPINNER)

    private val header = label {
        addClass(Styles.header)
        showWhen {
            viewModel.stationsViewStateProperty.isNotEqualTo(StationsViewState.NoResults)
        }
    }

    private val noResultsText = text {
        addClass(Styles.header)
        wrappingWidth = 350.0
        textAlignment = TextAlignment.CENTER

        showWhen {
            viewModel.stationsViewStateProperty.isEqualTo(StationsViewState.NoResults)
        }
    }

    private val subHeader = label {
        addClass(Styles.grayLabel)
    }

    override val root = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0
        add(header)
        add(noResultsText)
        add(subHeader)

        showWhen {
            viewModel.stationsViewStateProperty.isNotEqualTo(StationsViewState.Normal)
        }
    }

    init {
        viewModel.stationsViewStateProperty.onChange {
            when (it) {
                StationsViewState.NoResults -> showNoResults()
                StationsViewState.Error -> showMessage(messages["connectionError"], messages["connectionErrorDesc"], errorGlyph)
                StationsViewState.Loading -> showMessage("", "", loadingGlyph)
                StationsViewState.ShortQuery -> showMessage(messages["searchingLibrary"], messages["searchingLibraryDesc"], searchGlyph)
                else -> Unit
            }
        }
    }

    private fun showNoResults() {
        val library = libraryViewModel.selectedProperty.value
        if (library != null) {
            noResultsText.text =
                    if (library.params.isNullOrEmpty()) {
                        messages["noResults"]
                    } else {
                        "${messages["noResultsFor"]} \"${library.params}\""
                    }
        } else {
            //Selected library should have some value,
            //probably some error during loading occured here
            viewModel.stationsViewStateProperty.value = StationsViewState.Error
        }
    }

    private fun showMessage(headerValue: String, subHeaderValue: String, glyph: Glyph? = null) {
        with(header) {
            graphic = glyph
            text = headerValue
        }
        subHeader.text = subHeaderValue
    }
}
