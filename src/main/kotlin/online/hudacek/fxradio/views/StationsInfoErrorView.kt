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
import online.hudacek.fxradio.extension.glyph
import online.hudacek.fxradio.extension.showWhen
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.StationsViewModel
import online.hudacek.fxradio.viewmodel.StationsViewState
import org.controlsfx.glyphfont.FontAwesome
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
            booleanBinding(viewModel.stationViewStatus) {
                when (value) {
                    StationsViewState.Normal -> false
                    else -> true
                }
            }
        }
    }

    init {
        viewModel.stationViewStatus.onChange {
            when (it) {
                StationsViewState.NoResults -> showNoResults(libraryViewModel.selectedProperty.value.params)
                StationsViewState.Error -> showError()
                StationsViewState.Loading -> showLoading()
                StationsViewState.ShortQuery -> showShortQuery()
                else -> Unit
            }
        }
    }

    private fun showShortQuery() {
        noResultsText.hide()
        with(header) {
            show()
            text = messages["searchingLibrary"]
            graphic = searchGlyph
        }
        subHeader.text = messages["searchingLibraryDesc"]
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

    private fun showError() {
        noResultsText.hide()
        with(header) {
            show()
            graphic = errorGlyph
            text = messages["connectionError"]
        }
        subHeader.text = messages["connectionErrorDesc"]
    }

    private fun showLoading() {
        noResultsText.hide()
        with(header) {
            show()
            graphic = loadingGlyph
            text = messages["library.loading"]
        }
        subHeader.text = ""
    }
}
