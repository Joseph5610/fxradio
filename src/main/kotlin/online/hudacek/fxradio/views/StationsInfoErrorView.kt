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
class StationsInfoErrorView : View() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val searchGlyph = glyph(FontAwesome.Glyph.SEARCH)
    private val errorGlyph = glyph(FontAwesome.Glyph.WARNING)
    private val loadingGlyph = glyph(FontAwesome.Glyph.SPINNER)

    private val header = label {
        addClass(Styles.header)
    }

    private val noResultsText = text {
        addClass(Styles.header)
        wrappingWidth = 350.0
        textAlignment = TextAlignment.CENTER
        hide()
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
            booleanBinding(viewModel.stationsViewStateProperty) {
                when (value) {
                    StationsViewState.Normal -> false
                    else -> true
                }
            }
        }
    }

    init {
        viewModel.stationsViewStateProperty.onChange {
            when (it) {
                StationsViewState.NoResults -> showNoResults(libraryViewModel.selectedProperty.value.params)
                StationsViewState.Error -> showMessage(messages["connectionError"], messages["connectionErrorDesc"], errorGlyph)
                StationsViewState.Loading -> showMessage(messages["library.loading"], "", loadingGlyph)
                StationsViewState.ShortQuery -> showMessage(messages["searchingLibrary"], messages["searchingLibraryDesc"], searchGlyph)
                else -> Unit
            }
        }
    }

    private fun showNoResults(query: String?) {
        noResultsText.show()
        header.hide()
        if (query == null) {
            subHeader.text = messages["noResultsDesc"]
        } else {
            subHeader.text = null
        }

        noResultsText.text =
                if (query.isNullOrEmpty()) {
                    messages["noResults"]
                } else {
                    "${messages["noResultsFor"]} \"$query\""
                }
    }

    private fun showMessage(headerValue: String, subHeaderValue: String, glyph: Glyph? = null) {
        noResultsText.hide()
        with(header) {
            show()
            graphic = glyph
            text = headerValue
        }
        subHeader.text = subHeaderValue
    }
}
