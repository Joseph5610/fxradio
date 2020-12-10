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
package online.hudacek.fxradio.ui.view.stations

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.ui.viewmodel.StationsViewModel
import online.hudacek.fxradio.ui.viewmodel.StationsViewState
import online.hudacek.fxradio.utils.make
import online.hudacek.fxradio.utils.showWhen
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsEmptyView : View() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val searchGlyph by lazy { FontAwesome.Glyph.SEARCH.make() }
    private val errorGlyph by lazy { FontAwesome.Glyph.SEARCH.make() }

    private val headerTextProperty = viewModel.viewStateProperty.stringBinding {
        when (it) {
            StationsViewState.Error -> messages["connectionError"]
            StationsViewState.ShortQuery -> messages["searchingLibrary"]
            StationsViewState.Empty -> {
                val params = libraryViewModel.selectedProperty.value.params
                if (params.isEmpty()) {
                    messages["noResults"]
                } else {
                    "${messages["noResultsFor"]} \"$params\""
                }
            }
            else -> ""
        }
    }

    private val headerGraphicProperty = viewModel.viewStateProperty.objectBinding {
        when (it) {
            StationsViewState.Error -> errorGlyph
            StationsViewState.ShortQuery -> searchGlyph
            else -> null
        }
    }

    private val subHeaderTextProperty = viewModel.viewStateProperty.stringBinding {
        when (it) {
            StationsViewState.Error -> messages["connectionErrorDesc"]
            else -> ""
        }
    }

    private val header by lazy {
        text {
            bind(headerTextProperty)

            id = "stationMessageHeader"
            wrappingWidth = 350.0
            textAlignment = TextAlignment.CENTER

            addClass(Styles.header)
            addClass(Styles.defaultTextColor)
        }
    }

    //Description of a message, shown only if relevant
    private val subHeader by lazy {
        label(subHeaderTextProperty) {
            id = "stationMessageSubHeader"
            addClass(Styles.grayLabel)
        }
    }

    override val root = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0
        glyph {
            graphicProperty().bind(headerGraphicProperty)
        }

        add(header)
        add(subHeader)

        showWhen {
            viewModel.viewStateProperty.isNotEqualTo(StationsViewState.Normal)
        }
    }
}
