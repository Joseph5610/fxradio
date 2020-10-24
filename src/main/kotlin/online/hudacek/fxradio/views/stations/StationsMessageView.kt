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
package online.hudacek.fxradio.views.stations

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.glyph
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.StationsViewModel
import online.hudacek.fxradio.viewmodel.StationsViewState
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsMessageView : View() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val searchGlyph by lazy { glyph(FontAwesome.Glyph.SEARCH) }
    private val errorGlyph by lazy { glyph(FontAwesome.Glyph.WARNING) }

    private val headerTextProperty = viewModel.viewStateProperty.stringBinding {
        when (it) {
            StationsViewState.Error -> messages["connectionError"]
            StationsViewState.ShortQuery -> messages["searchingLibrary"]
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
            StationsViewState.ShortQuery -> messages["searchingLibraryDesc"]
            else -> ""
        }
    }

    private val noResultsTextProperty = libraryViewModel.selectedProperty.stringBinding {
        it?.let {
            if (it.params.isEmpty()) {
                messages["noResults"]
            } else {
                "${messages["noResultsFor"]} \"${it.params}\""
            }
        }
    }

    //Main live for message
    private val header by lazy {
        label(headerTextProperty) {
            id = "stationMessageHeader"
            graphicProperty().bind(headerGraphicProperty)
            addClass(Styles.header)
            showWhen {
                viewModel.viewStateProperty.isNotEqualTo(StationsViewState.NoResults)
            }
        }
    }

    private val noResultsText by lazy {
        text {
            addClass(Styles.header)
            wrappingWidth = 350.0
            textAlignment = TextAlignment.CENTER

            showWhen {
                viewModel.viewStateProperty.isEqualTo(StationsViewState.NoResults)
            }

            textProperty().bind(noResultsTextProperty)
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

        add(header)
        add(noResultsText)
        add(subHeader)
        
        showWhen {
            viewModel.viewStateProperty.isNotEqualTo(StationsViewState.Normal)
        }
    }
}
